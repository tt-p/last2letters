package Net;

import Util.GameLogic;
import Util.LogEvent;
import Util.LogEventListener;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Server {

    private final int port;
    private final int maxClients;
    private final Date date;
    private final List<EventListener> logListeners = new LinkedList<>();
    private final Queue<User> userList = new LinkedBlockingQueue<>();
    private final Queue<User> playerList = new LinkedBlockingQueue<>();
    private final TransportLayer protocol;
    private volatile boolean isRunning = true;
    private long numOfClients = 0;

    public Server(int port, TransportLayer protocol) {
        this(port, protocol, 10);
    }

    public Server(int port, TransportLayer protocol, int maxClients) {
        this.port = port;
        this.protocol = protocol;
        this.maxClients = maxClients;
        this.date = new Date();
    }

    public void start() {
        fireLogEvent(new LogEvent(this, String.format("%s - Server started", date)));

        new Thread(new RegisterUser()).start();
        new Thread(new HostAGame()).start();
        //new Thread(new Server.DebugThread()).start();
    }

    public void stop() {
        isRunning = false;
        for (User user: userList) {
            user.getConnection().stop();
        }
    }

    public void addLogListener(LogEventListener listener) {
        logListeners.add(listener);
    }

    public void removeLogListener(LogEventListener listener) {
        logListeners.remove(listener);
    }

    protected void fireLogEvent(LogEvent le) {
        for (EventListener lev : logListeners) {
            ((LogEventListener) lev).HandleLog(le);
        }
    }

    protected void fireLogEvents(LogEvent... les) {
        for (LogEvent le : les) {
            for (EventListener lev : logListeners) {
                ((LogEventListener) lev).HandleLog(le);
            }
        }
    }

    class RegisterUser implements Runnable {

        @Override
        public void run() {
            while (isRunning) {

                IConnection con;

                switch (protocol) {
                    case TCP:
                        con = new TCP_Connection(port);
                        break;
                    case UDP:
                        con = new UDP_Connection(port);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + protocol);
                }

                con.connectClient();

                String str = con.receiveMessage();

                if (str.startsWith(ApplicationLayer.REGISTER.code)) {

                    User u = new User(numOfClients, str.substring(2), con);
                    userList.add(u);
                    numOfClients++;

                    con.sendMessage(ApplicationLayer.CONNECTED.code);

                    fireLogEvent(new LogEvent(this,
                            String.format("%s - %s#%d is connected w/ IP %s", date, u.getName(), u.getId(), con.getAddress())));

                    new Thread(new HandleAClient(u)).start();
                }
            }
        }
    }

    class HandleAClient implements Runnable {

        private final IConnection con;
        private final User user;
        private boolean running = true;

        public HandleAClient(User user) {
            this.user = user;
            this.con = user.getConnection();
        }

        public void run() {

            while (running) {

                String str = con.receiveMessage();
                String res = ApplicationLayer.ERROR.code;

                switch (ApplicationLayer.convert(str)) {
                    case PLAY:
                        playerList.add(user);
                        res = ApplicationLayer.OK.code;
                        fireLogEvent(new LogEvent(this,
                                String.format("%s - %s#%d is added to play queue", date, user.getName(), user.getId())));
                        running = false;
                        break;
                    case CANCEL:
                        playerList.remove(user);
                        fireLogEvent(new LogEvent(this,
                                String.format("%s - %s#%d is removed from play queue", date, user.getName(), user.getId())));
                        break;
                    case QUIT:
                        userList.remove(user);
                        numOfClients--;
                        res = ApplicationLayer.OK.code;
                        fireLogEvent(new LogEvent(this,
                                String.format("%s - %s#%d is disconnected", date, user.getName(), user.getId())));
                        running = false;
                        break;
                }

                con.sendMessage(res);
            }
        }
    }

    class HostAGame implements Runnable {

        private User u1, u2;

        @Override
        public void run() {

            while (true) {

                if (playerList.size() < 2) continue;

                u1 = getAPlayer();

                fireLogEvent(new LogEvent(this,
                        String.format("%s - %s#%d is waiting for a game", date, u1.getName(), u1.getId())));

                u2 = getAPlayer();

                fireLogEvent(new LogEvent(this,
                        String.format("%s - %s#%d is waiting for a game", date, u2.getName(), u2.getId())));

                u1.getConnection().sendMessage(ApplicationLayer.OK.code);
                u2.getConnection().sendMessage(ApplicationLayer.OK.code);

                new Thread(new HandleAGame(u1, u2)).start();
            }
        }

        private User getAPlayer() {
            User user = null;
            while (user == null) {
                if (!playerList.isEmpty()) {
                    user = playerList.remove();
                }
            }
            return user;
        }
    }

    class HandleAGame implements Runnable {

        private final User u1;
        private final User u2;

        private final GameLogic game = new GameLogic();

        HandleAGame(User u1, User u2) {
            this.u1 = u1;
            this.u2 = u2;
        }

        @Override
        public void run() {

            fireLogEvent(new LogEvent(this,
                    String.format("%s - Game created for %s#%d and %s#%d", date, u1.getName(), u1.getId(), u2.getName(), u2.getId())));

            ApplicationLayer res1, res2;

            res1 = ApplicationLayer.convert(u1.getConnection().receiveMessage());
            res2 = ApplicationLayer.convert(u2.getConnection().receiveMessage());

            if (res1 != ApplicationLayer.START && res2 != ApplicationLayer.START) {
                u1.getConnection().sendMessage(ApplicationLayer.ERROR.code);
                u2.getConnection().sendMessage(ApplicationLayer.ERROR.code);
                return;
            }

            fireLogEvent(new LogEvent(this,
                    String.format("%s - Game started for %s#%d and %s#%d", date, u1.getName(), u1.getId(), u2.getName(), u2.getId())));

            while (!game.isGameEnded()) {
                doTurn();
            }

        }

        private String getInput(User user) throws IOException, InputMismatchException, InterruptedException, ExecutionException, TimeoutException {

            user.getConnection().sendMessage(ApplicationLayer.TURN.code);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> listener = executor.submit(() -> user.getConnection().receiveMessage());

            String input = listener.get(30_000, TimeUnit.MILLISECONDS);

            if (input.startsWith(ApplicationLayer.ACTION.code)) {
                input = input.substring(2);
            } else throw new InputMismatchException();

            return input;
        }

        private void doTurn() {
            User turn, other;

            if (game.getTurn() == GameLogic.Game.PLAYER_ONE) {
                turn = u1;
                other = u2;
            } else {
                turn = u2;
                other = u1;
            }

            String word;
            try {
                word = getInput(turn);

                sendFeedback(word);

                if (game.doAction(word) != GameLogic.Game.NONE) {
                    declareWinner(other, turn);
                }

            } catch (InputMismatchException | InterruptedException | ExecutionException | TimeoutException ex) {
                declareWinner(other, turn);
            } catch (IOException ex) {
                declareWinner(other, turn);
                game.endGame();
            }
        }

        private void declareWinner(User winner, User loser) {

            try {

                if (winner.getConnection().isConnected()) {
                    winner.getConnection().sendMessage(ApplicationLayer.END.code + "Congratulations! You won the game.");
                }

                if (loser.getConnection().isConnected()) {
                    loser.getConnection().sendMessage(ApplicationLayer.END.code + "You lost the game :(");
                }

                if (winner.getConnection().isConnected()) {
                    new Thread(new HandleAClient(winner)).start();
                }

                if (loser.getConnection().isConnected()) {
                    new Thread(new HandleAClient(loser)).start();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void sendFeedback(String word) {
            try {
                u1.getConnection().sendMessage(ApplicationLayer.ACTION.code + word);
                u2.getConnection().sendMessage(ApplicationLayer.ACTION.code + word);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    class DebugThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(20000);
                    System.out.println("DEBUG User List   : " + userList);
                    System.out.println("DEBUG Player List : " + playerList);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}

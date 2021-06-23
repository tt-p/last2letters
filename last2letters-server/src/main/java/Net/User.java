package Net;

public class User {

    private final long id;
    private String name;
    private final IConnection con;

    public User(long id, String name, IConnection connection) {
        this.id = id;
        this.name = name;
        this.con = connection;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IConnection getConnection() {
        return con;
    }

    @Override
    public String toString() {
        return id + "/" + name;
    }
}

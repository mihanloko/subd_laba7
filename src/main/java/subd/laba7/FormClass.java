package subd.laba7;

public class FormClass {
    private String id, name;

    public FormClass(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public FormClass() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FormClass{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

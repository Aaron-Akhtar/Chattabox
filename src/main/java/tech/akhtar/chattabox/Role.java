package tech.akhtar.chattabox;

public enum Role {
    NORMAL(0), MODERATOR(1), ADMINISTRATOR(2);

    int weight;
    Role(int weight){
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}

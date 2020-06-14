package tech.akhtar.chattabox;

public enum Role {
    NORMAL(0), MODERATOR(1), ADMINISTRATOR(2);

    int weight;
    Role(int weight){
        this.weight = weight;
    }

    /***
     * The Role Weight is what is used to compare permissions
     * and see what role has greater permissions then others.
     *
     * @return Role Weight (Integer), the higher the weight the more powerful the Role.
     */
    public int getWeight() {
        return weight;
    }
}

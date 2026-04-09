package entity;

public abstract class Animal {

    protected double weight;
    protected int maxCount;
    protected int speed;
    protected double foodNeeded;
    protected double foodEaten = 0;

    public abstract void eat();
    public abstract void move();
    public abstract void reproduce ();
    public abstract void dead ();
}
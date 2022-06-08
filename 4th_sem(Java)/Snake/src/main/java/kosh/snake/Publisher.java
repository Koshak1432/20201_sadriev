package kosh.snake;

public interface Publisher {
    void addSubscriber(Subscriber subscriber);
    void notifySubscribers();


}

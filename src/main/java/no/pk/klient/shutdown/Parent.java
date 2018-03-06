package no.pk.klient.shutdown;

public class Parent implements IShutdownThreadParent{

    private  ShutdownThread fShutdownThread;

    public Parent() {
        // Instantiate a new ShutdownThread instance and invoke the addShutdownHook method.
        fShutdownThread = new ShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(fShutdownThread);
    }


    @Override
    public void shutdown() {

    }
}

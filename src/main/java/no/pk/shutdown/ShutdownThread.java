package no.pk.shutdown;

public class ShutdownThread extends Thread{
    private IShutdownThread ShutdownThread;

    public ShutdownThread(IShutdownThread ShutdownThread) {
        this.ShutdownThread = ShutdownThread;
    }

    @Override
    public void run() {
        this.ShutdownThread.shutdown();
    }
}

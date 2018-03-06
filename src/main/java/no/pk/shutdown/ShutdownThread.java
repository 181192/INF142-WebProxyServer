package no.pk.shutdown;

public class ShutdownThread extends Thread{
    private IShutdownThread mShutdownThreadParent;

    public ShutdownThread(IShutdownThread mShutdownThreadParent) {
        this.mShutdownThreadParent = mShutdownThreadParent;
    }

    @Override
    public void run() {
        this.mShutdownThreadParent.shutdown();
    }
}

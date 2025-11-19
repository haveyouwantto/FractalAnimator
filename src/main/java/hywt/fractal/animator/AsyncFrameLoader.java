package hywt.fractal.animator;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import hywt.fractal.animator.keyframe.FractalImage;
import hywt.fractal.animator.keyframe.ImageLoader;

public class AsyncFrameLoader {
    private ImageLoader loader;
    private FractalImage currentImage;
    private Thread loaderThread;
    private BlockingQueue<Integer> imageQueue;
    public AsyncFrameLoader(ImageLoader loader) {
        this.loader = loader;
        this.imageQueue = new ArrayBlockingQueue<>(8);
        this.loaderThread = new Thread(() -> {
            while (true) {
                try {
                    int index = imageQueue.take();
                    System.out.println("[Async] Loading frame " + index);
                    currentImage = loader.acquire(index);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        this.loaderThread.setDaemon(true);
        this.loaderThread.start();
    }

    public void delegateLoad(int index) {
        try {
            imageQueue.put(index);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public FractalImage getCurrentImage() {
        return currentImage;
    }
}

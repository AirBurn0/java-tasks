package ru.mail.polis.homework.concurrency.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

/**
 * Нужно сделать свой executor с одним вечным потоком. Пока не вызовут shutdown или shutdownNow
 *
 * Задачи должны выполняться в порядке FIFO
 *
 * Max 6 баллов
 */
public class SingleExecutor implements Executor {

    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    private volatile Thread thread = null;

    private volatile boolean stop = false;

    private void run() {
        try {
            while (!stop || !tasks.isEmpty()) {
                tasks.take().run();
            }
       } catch (InterruptedException e) {
           return;
       }
    }
    
    /**
     * Метод ставит задачу в очередь на исполнение.
     * 3 балла за метод
     */
    @Override
    public void execute(Runnable command) {
        if (stop) {
            throw new RejectedExecutionException();
        }
        
        try {
            tasks.put(command);
        } catch (InterruptedException e) {
            return;
        }
        // запуск
        if (this.thread == null) {
            this.thread = new Thread(this::run);
            thread.start();
        }
    }
        

    /**
     * Дает текущим задачам выполниться. Добавление новых - бросает RejectedExecutionException
     * 1 балл за метод
     */
    public void shutdown() {
        stop = true;
    }

    /**
     * Прерывает текущие задачи. При добавлении новых - бросает RejectedExecutionException
     * 2 балла за метод
     */
    public void shutdownNow() {
        shutdown();
        thread.interrupt();
    }
}

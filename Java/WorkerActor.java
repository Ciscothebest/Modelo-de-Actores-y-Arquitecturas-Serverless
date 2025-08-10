// WorkerActor.java
package com.example.akka; // Mismo paquete que Messages

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class WorkerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private int taskCount = 0;

    public static Props props() {
        return Props.create(WorkerActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.ProcessTask.class, this::onProcessTask)
                .build();
    }

    private void onProcessTask(Messages.ProcessTask message) {
        taskCount++;
        log.info("Worker {} received task: {}", getSelf().path().name(), message.getTaskData());

        // Simular un fallo intencional cada 3 tareas
        if (taskCount % 3 == 0) {
            log.error("Worker {} intentionally failing for task: {}", getSelf().path().name(), message.getTaskData());
            throw new RuntimeException("Simulated worker failure!");
        }

        // Simular procesamiento de la tarea
        String result = "Processed: " + message.getTaskData().toUpperCase();
        getSender().tell(new Messages.TaskResult(result), getSelf());
    }

    @Override
    public void preStart() {
        log.info("Worker {} started", getSelf().path().name());
    }

    @Override
    public void postStop() {
        log.info("Worker {} stopped", getSelf().path().name());
    }

    @Override
    public void preRestart(Throwable reason, scala.Option<Object> message) {
        log.warning("Worker {} restarting due to: {}", getSelf().path().name(), reason.getMessage());
    }

    @Override
    public void postRestart(Throwable reason) {
        log.info("Worker {} restarted", getSelf().path().name());
    }
}

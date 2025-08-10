// SupervisorActor.java
package com.example.akka; // Mismo paquete que Messages

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.stop;

public class SupervisorActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorRef worker;

    public static Props props() {
        return Props.create(SupervisorActor.class);
    }

    // Definir la estrategia de supervisión
    private static final SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
                    match(RuntimeException.class, e -> restart()). // Reiniciar el worker en caso de RuntimeException
                    match(Exception.class, e -> stop()). // Detener el worker para otras excepciones
                    build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.ProcessTask.class, this::onProcessTask)
                .match(Messages.TaskResult.class, this::onTaskResult)
                .build();
    }

    @Override
    public void preStart() {
        log.info("SupervisorActor started. Creating WorkerActor...");
        // Crear un worker actor como hijo del supervisor
        worker = getContext().actorOf(WorkerActor.props(), "worker-1");
    }

    private void onProcessTask(Messages.ProcessTask message) {
        log.info("Supervisor received task: {}. Forwarding to worker...", message.getTaskData());
        worker.forward(message, getContext()); // Reenviar el mensaje al worker
    }

    private void onTaskResult(Messages.TaskResult message) {
        log.info("Supervisor received result from worker: {}", message.getResult());
        // Aquí podrías hacer algo con el resultado, como enviarlo de vuelta al cliente
        getSender().tell(message, getSelf()); // Reenviar el resultado al remitente original
    }
}

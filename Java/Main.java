// Main.java
package com.example.akka; // Mismo paquete que Messages

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        final ActorSystem system = ActorSystem.create("MicroserviceSystem");
        final ActorRef supervisor = system.actorOf(SupervisorActor.props(), "supervisor");

        System.out.println("Sending tasks to the supervisor...");

        for (int i = 1; i <= 5; i++) {
            String taskData = "Task " + i;
            // Usamos Patterns.ask para enviar un mensaje y esperar una respuesta
            Future<Object> future = Patterns.ask(supervisor, new Messages.ProcessTask(taskData), Duration.create(5, TimeUnit.SECONDS));
            try {
                Messages.TaskResult result = (Messages.TaskResult) Await.result(future, Duration.create(5, TimeUnit.SECONDS));
                System.out.println("Received result for " + taskData + ": " + result.getResult());
            } catch (Exception e) {
                System.err.println("Error processing " + taskData + ": " + e.getMessage());
            }
            Thread.sleep(500); // Pequeña pausa para observar los logs
        }

        // Esperar un poco antes de terminar el sistema
        Thread.sleep(2000);
        system.terminate();
        Await.result(system.whenTerminated(), Duration.create(10, TimeUnit.SECONDS));
        System.out.println("Actor system terminated.");
    }
}

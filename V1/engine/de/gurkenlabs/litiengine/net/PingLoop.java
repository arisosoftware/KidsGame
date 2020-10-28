package de.gurkenlabs.litiengine.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.gurkenlabs.litiengine.ILaunchable;
import de.gurkenlabs.litiengine.net.messages.ClientMessage;
import de.gurkenlabs.litiengine.net.messages.ClientMessageHandler;
import de.gurkenlabs.litiengine.net.messages.IMessageHandlerProvider;
import de.gurkenlabs.litiengine.net.messages.MessagePacket;
import de.gurkenlabs.litiengine.net.messages.MessageType;
import de.gurkenlabs.litiengine.net.messages.PingResponseMessage;

public class PingLoop extends ClientMessageHandler<PingResponseMessage> implements ILaunchable {
  private static final Logger log = Logger.getLogger(PingLoop.class.getName());
  private final int clientId;
  private PingThread pingThread;

  private final List<LongConsumer> pingRecordConsumer;

  private final int port;

  private final IPacketSender sender;

  private final String serverIpAdress;

  public PingLoop(final int clientId, final IMessageHandlerProvider provider, final IPacketSender sender, final String serverIpAdress, final int port) {
    this.pingRecordConsumer = new ArrayList<>();
    this.clientId = clientId;
    this.sender = sender;
    this.serverIpAdress = serverIpAdress;
    this.port = port;
    provider.register(MessageType.PING, this);
  }

  public void onPingRecorded(final LongConsumer consumer) {
    if (this.pingRecordConsumer.contains(consumer)) {
      return;
    }

    this.pingRecordConsumer.add(consumer);
  }

  @Override
  public void start() {
    this.pingThread = new PingThread();
    this.pingThread.start();
  }

  @Override
  public void terminate() {
    this.pingThread.terminate();
  }

  @Override
  protected void handle(final PingResponseMessage message, final InetAddress address, final int port) {
    if (this.pingThread == null) {
      return;
    }

    try {
      if (address.getHostAddress().equals(InetAddress.getByName(this.serverIpAdress).getHostAddress())) {
        this.pingThread.pingAnswerReceived();
      }
    } catch (final UnknownHostException e) {
      log.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  private class PingThread extends Thread implements ILaunchable {
    private static final int TIME_BETWEEN_PINGS = 1000;

    private boolean isTerminated;

    private long lastPing;

    private PingThread() {
    }

    public void pingAnswerReceived() {

      final long after = System.currentTimeMillis();
      final long ping = after - this.lastPing;

      for (final LongConsumer consumer : PingLoop.this.pingRecordConsumer) {
        consumer.accept(ping);
      }
    }

    @Override
    public void run() {
      while (!this.isTerminated) {
        this.lastPing = System.currentTimeMillis();
        final MessagePacket<ClientMessage> packet = new MessagePacket<>(MessageType.PING, new ClientMessage(PingLoop.this.clientId));
        PingLoop.this.sender.sendData(packet, PingLoop.this.serverIpAdress, PingLoop.this.port);

        try {
          Thread.sleep(TIME_BETWEEN_PINGS);
        } catch (final InterruptedException e) {
          log.log(Level.WARNING, e.getMessage(), e);
          this.interrupt();
        }
      }
    }

    @Override
    public void terminate() {
      this.isTerminated = true;
    }
  }
}
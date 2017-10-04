/**
 * 
 */
package de.evoila.cf.broker.service.availability;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.ServerAddress;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class ServicePortAvailabilityVerifier {

	private static final int SOCKET_TIMEOUT = 30000;

	private static final int INITIAL_TIMEOUT = 150 * 1000;

	private static final int connectionTimeouts = 10;

	private final Logger log = LoggerFactory.getLogger(ServicePortAvailabilityVerifier.class);

	public void timeout(int timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e1) {
			log.info("Starting new timeout interval was interrupted.", e1);
		}
	}

	public boolean execute(String ip, int port) {
		boolean available = false;

		log.info("Verifying port availability on: {}:{}", ip, port);
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(ip, port), SOCKET_TIMEOUT);

			if (socket.isConnected())
				available = true;
			else
				timeout(SOCKET_TIMEOUT);
		} catch (Exception e) {
			log.info("Service port could not be reached", e);

			timeout(SOCKET_TIMEOUT);
		} finally {
			if (socket != null && socket.isConnected()) {
				try {
					socket.close();
				} catch (IOException e) {
					log.info("Could not close port", e);
				}
			}
		}
		return available;
	}

	public boolean verifyServiceAvailability(String ip, int port, boolean useInitialTimeout) throws PlatformException {
		boolean available = false;
		
		if (useInitialTimeout) 
			this.timeout(INITIAL_TIMEOUT);
		
		for (int i = 0; i < connectionTimeouts; i++) {
			available = this.execute(ip, port);

			log.info("Service Port availability: {}", available);

			if (available) {
				break;
			}
		}
		log.info("Service Port availability (last status during request): {}", available);
		return available;
	}

	public boolean verifyServiceAvailability(List<ServerAddress> serverAddresses, boolean useInitialTimeout) throws PlatformException {
		for (ServerAddress serverAddress : serverAddresses) {
			if (!verifyServiceAvailability(serverAddress.getIp(), serverAddress.getPort(), useInitialTimeout)) {
				return false;
			}
		}
		return true;
	}

}

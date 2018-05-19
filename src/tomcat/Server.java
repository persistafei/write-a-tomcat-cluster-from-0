package tomcat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tomcat.context.ServerContext;

public class Server {
	
	ServerSocket socket;
	
	public Server() throws IOException {
		socket= new ServerSocket(ServerContext.port);
	}
	
	public void start(){
		ExecutorService executorService= Executors.newFixedThreadPool(ServerContext.pool);
		while(true){
			try {
				Socket client= socket.accept();
				ClientHandler clientHandler= new ClientHandler(client);
				executorService.execute(clientHandler);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		Server s= new Server();
		s.start();
	}

}

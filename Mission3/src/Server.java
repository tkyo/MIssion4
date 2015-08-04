import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 簡易HTTPサーバ
 *
 */
public class Server {

	private static final int PORT = 8080;
	private static String requestPath;

	public static void main(String[] args) throws IOException {
		System.out.println("start up http server...");

		ServerSocket serverSocket = null;
		Socket socket = null;

		serverSocket = new ServerSocket(PORT);

		try {
			while (true) {

				socket = serverSocket.accept();
				System.out.println("request incoming...");

				// リクエストを解析
				CheackRequest(socket);

				// レスポンスを生成
				CreateResponse(socket);

			}
		} finally {
			if (socket != null) {
				socket.close();
			}
			if(serverSocket != null){
				serverSocket.close();
			}
		}

	}

	/**
	 * リクエスト文から要求されたファイル名を読み取り、リクエスト文を出力します
	 *
	 * @param socket
	 */
	private static void CheackRequest(Socket socket) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));

		// 1行目からリクエストパスを取得しておく(/でアクセスされた場合はindex.htmlを表示）
		String inline = br.readLine();
		requestPath = inline.split(" ")[1];
		if (requestPath.equals("/")) {
			requestPath = "index.html";
		}

		// リクエストを出力
		while (br.ready() && inline != null) {
			System.out.println(inline);
			inline = br.readLine();
		}

	}

	/**
	 * レスポンス文を生成し、レスポンス文を出力します
	 *
	 * @param socket
	 */
	private static void CreateResponse(Socket socket) throws IOException {
		StringBuilder builder = new StringBuilder();
		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader reader = null;
		String inline = null;
		;

		try {

			// レスポンの生成
			reader = new BufferedReader(new FileReader("WebContent/"
					+ requestPath));
			builder.append("HTTP/1.1 200 OK").append("\n");
			builder.append("Content-Type: text/html").append("\n");
			builder.append("\n");
			while ((inline = reader.readLine()) != null) {
				builder.append(inline);
			}
			writer.println(builder.toString());

			// レスポンスの出力
			System.out.println("responce...");
			System.out.println(builder.toString() + "\n");

		} catch (FileNotFoundException e) {
			// レスポンスの生成
			builder.append("HTTP/1.1 404 Not Found").append("\n");
			builder.append("Content-Type: text/html; charset=UTF-8").append(
					"\n");
			builder.append("\n");
			builder.append("<html><head><title>HTTP 404 未検出</title></head><body><h1>指定したファイルは存在しません。</h1></body></html>");
			writer.println(builder.toString());

			// レスポンスの出力
			System.out.println("responce...");
			System.out.println(builder.toString() + "\n");

		} finally {
			if (writer != null) {
				writer.close();
			}
			if (reader != null) {
				reader.close();
			}
		}

	}

}
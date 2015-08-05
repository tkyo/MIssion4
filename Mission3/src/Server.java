import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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

				// リクエストを解析
				CheackRequest(socket);

				// レスポンスを生成
				CreateResponse(socket);

			}
		} finally {
			if (socket != null) {
				socket.close();
			}
			if (serverSocket != null) {
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
		if (inline == null) {
			requestPath = null;
		} else {
			requestPath = inline.split(" ")[1];
			if (requestPath.equals("/")) {
				requestPath = "index.html";
			}

			// リクエストを出力
			System.out.println("request incoming...");
			while (br.ready() && inline != null) {
				System.out.println(inline);
				inline = br.readLine();
			}
		}

	}

	/**
	 * レスポンス文を生成し、レスポンス文を出力します
	 *
	 * @param socket
	 */
	private static void CreateResponse(Socket socket) throws IOException {
		StringBuilder builder = new StringBuilder();
		String inline = null;
		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader reader = null;
		PrintStream ps = new PrintStream(socket.getOutputStream(), true);
		FileInputStream fs = null;

		if (requestPath == null) {
			return;
		}

		try {
			if (requestPath.endsWith(".jpg")) {
				// レスポンスの生成(画像が要求された場合)
				builder.append("HTTP/1.1 200 OK").append("\n");
				builder.append("Content-Type: img/jpg").append("\n");
				File file = new File("WebContent/" + requestPath);
				int length = (int) file.length();
				byte[] bytes = new byte[length];
				fs = new FileInputStream(file);
				writer.println(builder.toString());
				fs.read(bytes);
				ps.write(bytes, 0, length);
				ps.flush();
				writer.println(builder.toString());

			} else {
				reader = new BufferedReader(new FileReader("WebContent/"
						+ requestPath));

				// レスポンの生成
				builder.append("HTTP/1.1 200 OK").append("\n");
				builder.append("Content-Type: text/html").append("\n");
				builder.append("\n");
				while ((inline = reader.readLine()) != null) {
					builder.append(inline);
				}
				writer.println(builder.toString());

			}

		} catch (FileNotFoundException e) {
			// レスポンスの生成
			builder.append("HTTP/1.1 404 Not Found").append("\n");
			builder.append("Content-Type: text/html; charset=UTF-8").append(
					"\n");
			builder.append("\n");
			builder.append("<html><head><title>HTTP 404 未検出</title></head><body><h1>指定したファイルは存在しません。</h1></body></html>");
			writer.println(builder.toString());

		} finally {
			// レスポンスの出力
			System.out.println("responce...");
			System.out.println(builder.toString() + "\n");

			if (ps != null) {
				ps.close();
			}

			if (fs != null) {
				fs.close();
			}

			if (writer != null) {
				writer.close();
			}
			if (reader != null) {
				reader.close();
			}
		}

	}

}
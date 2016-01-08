import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;

public class SerialExample3 {
	public static void write() throws InterruptedException {
		OutputStream out;
		try {
            Thread.sleep(2000);// 链接后暂停2秒再继续执行
            System.out.println("connected!");
            // 进行输入输出操作
            OutputStreamWriter writer = new OutputStreamWriter(out);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write("5000");
            bw.flush();
            bw.close();
            writer.close();
        }catch(IOException e){
        	e.printStackTrace();
        }
	}

	

	public static void main(String args[]) throws InterruptedException {
		System.out.println("<--Pi4J--> Serial Communication Example ...started.");
		System.out.println(" ... connect using settings: device: /dev/ttyACM0/ baudRate: 9600");
		System.out.println(" ... data received on serial port should bedisplayed below.");
		String device = "/dev/ttyACM0";
		int baudRate = 9600;
		String ID = "0001";
		
		StringBuilder sb = new StringBuilder();

		final Serial serial = SerialFactory.createInstance();
		try {
			serial.open(device, baudRate);
			write();

			while (true) {
				try {
					Socket socket = new Socket("117.16.146.58", 55555);
					Date day = new Date();
					SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					char temp = serial.read();

					if (temp != '\n') {
						sb.append(temp);

					} else {
						String buf = sb.toString();

						System.out.print(buf);
						System.out.println("@Id:" + ID + ",timestamp:" + time.format(day) + ",Temp:" + buf);
						String datastream = "@Id:" + ID + ",timestamp:" + time.format(day) + ",Temp:" + buf;

						oos.writeObject(datastream);
						oos.flush();
						oos.close();
						sb = new StringBuilder();
					}
				} catch (IllegalStateException ex) {
					ex.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Thread.sleep(100);

			}

		} catch (SerialPortException ex) {
			System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
			return;
		}
	}
}

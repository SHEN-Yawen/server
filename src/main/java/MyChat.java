import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class MyChat {
    public JTextField tx;        // 文本输入框
    public JTextArea ta;         // 文本显示区域
    public String login ; // 用户名
    private BufferedWriter writer; // 用于向服务器发送消息的写入器
    private BufferedReader reader; // 用于接收服务器发送的消息的读取器

    public MyChat(String l) {
        login = l;

        // 创建窗口和 GUI 组件
        JFrame f = new JFrame("My Chat");
        f.setSize(400, 400);

        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());

        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());

        tx = new JTextField();
        p1.add(tx, BorderLayout.CENTER);

        JButton sendButton = new JButton("发送");
        p1.add(sendButton, BorderLayout.EAST);

        ta = new JTextArea();
        ta.setEditable(false); // 设置文本区域为只读
        p2.add(new JScrollPane(ta), BorderLayout.CENTER);
        p2.add(p1, BorderLayout.SOUTH);

        f.setContentPane(p2);

        try {
            // 创建与服务器的套接字连接
            Socket socketClient = new Socket("localhost", 5555);
            writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add an action listener for the Send button
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                String message = login + " : " + tx.getText();
                tx.setText("");
                try {
                    // Send the message to the server
                    writer.write(message);
                    writer.write("\r\n");
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 创建一个线程来持续读取和显示接收到的消息
        Thread messageReceiver = new Thread(new Runnable() {
            public void run() {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        appendMessage(message); // 将接收到的消息追加到 JTextArea
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        messageReceiver.start();

        f.setVisible(true);
    }

    // 将消息附加到 JTextArea 中的方法
    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            ta.append(message + "\n");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MyChat("Imed");
            }
        });
    }
}




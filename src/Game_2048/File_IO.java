package Game_2048;
import java.io.*;

public class File_IO {
	String path = "Ranking.txt";
	BufferedReader br;
	BufferedWriter bw;
	File folder = new File("./");
	
	public File_IO()
	{
		try {
			bw = new BufferedWriter(new FileWriter(path,true));
			bw.close();
		} catch(IOException e) {}
	}
	
	public void saveFile(String contents)
	{
		try {
			bw = new BufferedWriter(new FileWriter(path,true));
			bw.write(contents);
			bw.flush();
			bw.close();
		} catch(IOException e) {};
	}
	
	public void rankreset()
	{
		try {
			bw = new BufferedWriter(new FileWriter(path));
			bw.write("");
			bw.flush();
			bw.close();
		} catch(IOException e) {};
	}
	
	public String read_word()
	{
		String str = "";
		String temp = "";
		
		try {
			br = new BufferedReader(new FileReader(path));
			while((temp = br.readLine()) != null)
				str = str + temp+"\n";
		} catch(IOException e) {}
		
		return str;
	}
	
	public String[] import_file()
	{
		File[] list = folder.listFiles();
		String[] str = new String[list.length];
		for(int i = 0; i<str.length; i++)
			str[i] = list[i].getName();
		return str;
	}
}

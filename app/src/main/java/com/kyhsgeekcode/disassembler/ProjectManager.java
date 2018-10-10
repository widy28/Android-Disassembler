package com.kyhsgeekcode.disassembler;
import android.util.*;
import java.io.*;
import java.util.*;
import com.codekidlabs.storagechooser.utils.*;

public class ProjectManager
{
	public static final String Path="/sdcard/Android Disassembler/";
	public static final String TAG="Disassembler Proj";

	String[] strprojects=new String[]{"Create new"};
	OnProjectOpenListener listener;
	
	//https://stackoverflow.com/a/8955040/8614565
	public ProjectManager(OnProjectOpenListener list) throws IOException
	{
		listener=list;
		File file=new File(Path);
		if (!file.exists() || !file.isDirectory())
		{
			file.mkdirs();
			Log.v(TAG, "Initial load");
			return;
		}
		File[] files=file.listFiles();
		ArrayList<IOException> ex=new ArrayList<>();
		for (File f:files)
		{
			if (f.isDirectory())
			{
				try
				{
					Project proj=new Project(f.getName());
					projects.put(proj.name, proj);
				}
				catch (IOException e)
				{
					ex.add(e);
				}		
			}
		}
		strprojects = projects.keySet().toArray(new String[projects.size()]);
		if(ex.isEmpty())
			return;
		IOException firstex=ex.remove(0);
		for(IOException e:ex)
		{
			firstex.addSuppressed(e);
		}
		throw firstex;
	}

	HashMap<String,Project> projects=new HashMap<>();

	public String[] strProjects()
	{
		// TODO: Implement this method
		return strprojects;
	}
	public Project newProject(String name, String path) throws IOException
	{
		Project proj=new Project(name);
		
		return ;
	}
	public Project Open(String name)
	{
		Project proj=projects.get(name);
		if(proj!=null)
		{
			proj.Open();
			return proj;
		}
		return proj;
	}
	public class Project
	{
		//Files: .project, disasm, details
		String name="";
		String oriFilePath="";
		File projFile;
		//String 
		public Project(String name) throws IOException
		{
			this.name = name;
			File file=new File(Path);
			File projdir=new File(file,name+"/");
			projFile = new File(projdir, name + ".adp");
			if (projFile.exists())
			{
				HashMap<String,String> map=new HashMap<String,String>();
				BufferedReader br=new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null)
				{
					String [] parsed=line.split(":");
					String key=parsed[0];
					String value=parsed[1];
					map.put(key, value);
				}
				br.close();
				oriFilePath = map.get("oriPath");
				if(oriFilePath==null)
					oriFilePath="";
			}
			else
			{
					projFile.createNewFile();
					oriFilePath = "";
			}
		}

		public void setOriFilePath(String oriFilePath)
		{
			this.oriFilePath = oriFilePath;
		}

		public String getOriFilePath()
		{
			return oriFilePath;
		}
		//loads the data.
		public void Open()
		{
			File dir=new File(Path);
			File projdir=new File(dir,name+"/");
			detail=new File(projdir,"details.txt");
			disasm=new File(projdir,"disasm.json");
			listener.onOpen(this);
		}
		public void Save() throws IOException
		{
			BufferedWriter bw=new BufferedWriter(new FileWriter(projFile));
			bw.write("oriPath:" + oriFilePath);
			bw.close();
		}
		File detail;
		File disasm;
	}

	public interface OnProjectOpenListener
	{
		public void onOpen(Project proj);
	}
}

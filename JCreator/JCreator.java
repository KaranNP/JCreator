import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

class JCreator extends JFrame implements ActionListener,WindowListener
{
	JFrame f;
	JTextArea jt1,jt2;
	JScrollPane jsp1,jsp2;
	JPanel jp1,jp2;
	JSplitPane sp;
	JMenuBar mb;
	JMenu m_file,m_edit,m_build;
	JMenuItem m_new,m_open,m_save,m_saveas,m_close,m_closeall, m_find, m_findreplace,m_cut,m_copy,m_paste,m_compile,m_run,m_dir;			// dir is there to check how to run dos commands from java
	JTabbedPane jtp;
	JOptionPane jop_save;
	FindDialog fd=null;
	ReplaceDialog fr = null;
	
	/* Variables in program */
	Vector vpath,vcontent;
	int active_tab=0;				// used in function isTabOpen.
	boolean exitSystem=false;
	String outputstr="",s2="";
	boolean noerror = false;
	
	/* For find */
	Pattern fpattern;
	Matcher fmatcher;	
	boolean isFind=true; // it will be true if find operation is successful
	
	/*For close all*/
	Closeall closeall;
	JFrame cframe;
	JList clist;
	DefaultListModel clistmodel;
	
	/*FileFilter*/
	FileFilter filter;
	
	public JCreator()
	{
		JFrame f = new JFrame();
		f.setSize(800,600);
		
		jtp = new JTabbedPane(JTabbedPane.TOP);
		//jp2 = new JPanel(new GridLayout(1,0));
		
		//jtp.addTab("Untitled",new JTextArea());
		//jtp.addTab("Untitled",new Label("Welcome to JCreator"));
		
		jt2 = new JTextArea();
		//jt2.setEditable(false);
		jsp1 = new JScrollPane(jtp);
		jsp2 = new JScrollPane(jt2);
		
		//jp2.add(jsp2);
		
		mb = new JMenuBar();
		m_file = new JMenu("File");
		m_edit = new JMenu("Edit");
		m_build = new JMenu("Build");
		
		m_new = new JMenuItem("New");
		m_open = new JMenuItem("Open");
		m_save = new JMenuItem("Save");
		m_saveas = new JMenuItem("Save As");
		m_close = new JMenuItem("Close");
		m_closeall = new JMenuItem("Close All");
		
		m_find = new JMenuItem("Find");
		m_findreplace = new JMenuItem("Find and Replace");
		m_cut = new JMenuItem("Cut");
		m_copy = new JMenuItem("Copy");
		m_paste = new JMenuItem("Paste");
		
		//m_cut.setEnabled(false);
		
		m_compile = new JMenuItem("Compile");
		m_run = new JMenuItem("Run");
		m_dir = new JMenuItem("Directory");
		
		m_file.add(m_new);
		m_file.add(m_open);
		m_file.add(m_save);
		m_file.add(m_saveas);
		m_file.add(m_close);
		m_file.add(m_closeall);
		
		m_edit.add(m_find);
		m_edit.add(m_findreplace);
		m_edit.add(m_cut);
		m_edit.add(m_copy);
		m_edit.add(m_paste);
		
		m_build.add(m_compile);
		m_build.add(m_run);
		m_build.add(m_dir);		
		
		mb.add(m_file);
		mb.add(m_edit);
		mb.add(m_build);	
		
		sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,jsp1,jsp2);
		sp.setDividerSize(2);
		sp.setDividerLocation(450);
		
		jop_save = new JOptionPane("Do you want to save?",JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_CANCEL_OPTION);
		
		/*Adding action events */
		m_new.addActionListener(this);
		m_open.addActionListener(this);
		m_save.addActionListener(this);
		m_saveas.addActionListener(this);
		m_close.addActionListener(this);
		m_closeall.addActionListener(this);
		
		m_find.addActionListener(this);
		m_findreplace.addActionListener(this);
		m_cut.addActionListener(this);
		m_copy.addActionListener(this);
		m_paste.addActionListener(this);
		
		m_compile.addActionListener(this);
		m_run.addActionListener(this);
		m_dir.addActionListener(this);
		
		/*Adding Vector*/
		vpath = new Vector();	
		vcontent = new Vector();
				
		f.add(sp);
		f.add(mb,BorderLayout.NORTH);
		f.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		m_save.setEnabled(false);
		m_saveas.setEnabled(false);
		m_close.setEnabled(false);
		m_closeall.setEnabled(false);
		m_find.setEnabled(false);
		m_findreplace.setEnabled(false);
		m_cut.setEnabled(false);
		m_copy.setEnabled(false);
		m_paste.setEnabled(false);
		m_compile.setEnabled(false);
		m_run.setEnabled(false);
		m_dir.setEnabled(false);
		
		f.addWindowListener(this);
		f.setVisible(true);
		
		/* File name extension filter defined :*/
		filter = new FileNameExtensionFilter("Java file","java");
	}
		
	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
	
	public void windowClosing(WindowEvent e)
	{
		System.out.println("windowClosing called");
		exitSystem = true;
		if(closeall==null)
			closeall = new Closeall();
		else
			closeall.updatelist();
		if(vpath.size()==0)
			System.exit(1);
	}	
	
	public void actionPerformed(ActionEvent e)
	{
		String str = e.getActionCommand();
		
		Object ae = e.getSource();
		
		if(ae == m_new)
		{
			fnew();
			System.out.println("New is clicked");
		}
		if(ae == m_open)
		{
			System.out.println("Open  is clicked");
			fopen();
		}
		
		if(ae==m_save)
		{
			fsave();
			System.out.println("Save is clicked");
		}
		
		if(ae==m_saveas)
		{
			System.out.print("Save As is clicked");
			fsaveas();
		}
		if(ae==m_close)
		{
			fclose();
			System.out.print("Close is clicked");
		}
		if(ae==m_closeall)
		{
			System.out.print("Close All is clicked");
			if(closeall==null)
				closeall = new Closeall();
			else
				closeall.updatelist();
		}
		if(ae==m_find)
		{
			if(vpath.size()>0)
			{
				System.out.print("Find is clicked");
				if(fd==null)
				{
					fd = new FindDialog();
					try
						{
							fr.setActive(false);
						}
						catch(Exception e1)
						{}
				}			
				else
				{
					try
					{
						System.out.println("fd.setActive is called");
						fd.setActive(true);
						try
						{
							fr.setActive(false);
						}
						catch(Exception e1)
						{}
					}
					catch(Exception e1)
					{
						System.out.print("Exception at Find. Failed to open existing find dialog, creating a new one.");
						fd = new FindDialog();
					}
				}		
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Can't use find if no file is opened!", "Alert!!", JOptionPane.ERROR_MESSAGE);
			}
		}
		if(ae==m_findreplace)
		{
			//System.out.print("");
			if(vpath.size()>0)
			{
				//System.out.print("");
				if(fr==null)
				{
					fr = new ReplaceDialog();
						try
						{
							fd.setActive(false);
						}
						catch(Exception e1)
						{}
				}
				else
				{
					try
					{
						System.out.println("fd.setActive is called");
						fr.setActive(true);
						try
						{
							fd.setActive(false);
						}
						catch(Exception e1)
						{}
					}
					catch(Exception e1)
					{
						System.out.print("Exception at Find. Failed to open existing find dialog, creating a new one.");
						fr = new ReplaceDialog();
					}
				}		
				
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Can't use find if no file is opened!", "Alert!!", JOptionPane.ERROR_MESSAGE);
			}
			
		}
		if(ae==m_cut)
		{
			System.out.print("Cut is clicked");
			JTextArea jta = (JTextArea)jtp.getSelectedComponent();
			jta.cut();
		}
		if(ae==m_copy)
		{
			System.out.print("Copy is clicked");
			JTextArea jta = (JTextArea)jtp.getSelectedComponent();
			jta.copy();
		}
		if(ae==m_paste)
		{
			System.out.print("Paste is clicked");
			JTextArea jta = (JTextArea)jtp.getSelectedComponent();
			jta.paste();
		}
		if(ae==m_compile)
		{
			System.out.print("Compile is clicked");
			fcompile();
		}
		if(ae==m_run)
		{
			System.out.print("Run is clicked");
			fcompile();
			frun();
		}
		if(ae==m_dir)
		{
			System.out.print("Directory is clicked");
			try
			{
				Process p = Runtime.getRuntime().exec("cmd /C dir");
				BufferedReader stdInput = new BufferedReader(new
				InputStreamReader(p.getInputStream()),8*1024);
				BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				// read the output from the command
				String s = null;
				System.out.println("Here is the standard output of the command:\n");
				while ((s = stdInput.readLine()) != null)
				System.out.println(s.replace("[","").replace("]",""));
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
		}
		
		/* if vpath > 0 then these button will be clickable and if it goes back to 0 they will be unclickable again*/
		if(vpath.size()>0)
		{
			m_save.setEnabled(true);
			m_saveas.setEnabled(true);
			m_close.setEnabled(true);
			m_closeall.setEnabled(true);
			m_find.setEnabled(true);
			m_findreplace.setEnabled(true);
			m_cut.setEnabled(true);
			m_copy.setEnabled(true);
			m_paste.setEnabled(true);
			m_compile.setEnabled(true);
			m_run.setEnabled(true);
			m_dir.setEnabled(true);
		}
		else
			if(vpath.size()==0)
			{
				m_save.setEnabled(false);
				m_saveas.setEnabled(false);
				m_close.setEnabled(false);
				m_closeall.setEnabled(false);
				m_find.setEnabled(false);
				m_findreplace.setEnabled(false);
				m_cut.setEnabled(false);
				m_copy.setEnabled(false);
				m_paste.setEnabled(false);
				m_compile.setEnabled(false);
				m_run.setEnabled(false);
				m_dir.setEnabled(false);
			}
		
	}
	
	public void fcompile()
	{
		fsave();
		try
		{
			String command = "cmd /C javac";
			//String command = "cmd /c start cmd.exe";
			int index = jtp.getSelectedIndex();
			File f = new File((String)vpath.get(index));
			String file_name = f.getAbsolutePath();
			System.out.println("\n"+file_name);
			
			String command2 = command+" "+file_name;
			noerror=false;
			//command2 = command2.replace(".java","");
			try
			{
				Process p = Runtime.getRuntime().exec(command2);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()),8*1024); // jo program generate karega ye wo hai
				BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream())); // agar koi error generate hui toh ye uske lie hai
				// read the output from the command
				String s = null;
				s2="";
				outputstr="";
				System.out.println("Here is the standard output of the command:\n");
				while ((s = stdError.readLine()) != null)
				{
					//System.out.println(s.replace("[","").replace("]",""));
					s2 = s2 + s.replace("[","").replace("]","");
				}
				if(s2.equals(""))
					noerror = true;
				s=null;
				while ((s = stdInput.readLine()) != null)
				{
					//System.out.println(s.replace("[","").replace("]",""));
					outputstr = s.replace("[","").replace("]","");
				}
				s2 = s2 + "\n" + outputstr;
				if(noerror)
					s2 = s2 + "\nCompile Successful\n";
				System.out.println("Length of s2"+s2.length());
				System.out.println(s2);
				jt2.setText(s2);				
				//if(s2.equals("nullnull"))
				//	jt2.setText("");
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
		}
		catch(Exception e1)
		{
				e1.printStackTrace();
		}		
	}
	
	public void frun()
	{
		if(noerror)
		{
			
			try
			{
				String command = "cmd /c start cmd /K java -cp";
				int index = jtp.getSelectedIndex();
				File f = new File((String)vpath.get(index));
				String file_name = f.getAbsolutePath();
				System.out.println("\n"+file_name);
				String file_name_small = f.getName();
				file_name = file_name.replace(file_name_small,"");
				System.out.println("Full name :\n"+file_name+"Small Name :\n"+file_name_small);
				command = command + " " + file_name + " " + file_name_small;
				String command2 = command+" "+file_name;
				command2 = command2.replace(".java","");
				try
				{
					Process p = Runtime.getRuntime().exec(command2);/*
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()),8*1024);
					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					// read the output from the command
					String s = null;
					String oldtext = jt2.getText();
					s2="";
					outputstr="";
					System.out.println("Here is the standard output of the command:\n");
					while ((s = stdError.readLine()) != null)
					{
						//System.out.println(s.replace("[","").replace("]",""));
						s2 = s2 + s.replace("[","").replace("]","");
					}
					s=null;
					while ((s = stdInput.readLine()) != null)
					{
						//System.out.println(s.replace("[","").replace("]",""));
						outputstr = s.replace("[","").replace("]","");
					}
					s2 = outputstr + "\n" + s2;
					System.out.println("Length of s2"+s2.length());
					System.out.println(s2);
					jt2.setText(oldtext+s2);				*/
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
			catch(Exception e1)
			{
					e1.printStackTrace();
			}
		}
	}
	
	public void fopen()
	{
		JFileChooser open = new JFileChooser();
		open.addChoosableFileFilter(filter);
		int check_open = open.showOpenDialog(jtp);
		File opened_file;
		if(check_open==JFileChooser.APPROVE_OPTION)
		{
			opened_file=open.getSelectedFile();
			String file_name=open.getName(opened_file);
			String file_path = opened_file.getAbsolutePath();
			/* checktab function here - if tab is already open, then true*/
			if(isTabOpen(file_path))
			{
				jtp.setSelectedIndex(active_tab);
			}
			else
			{
				JTextArea txt = new JTextArea();
				jtp.addTab(file_name,txt);
				vpath.add(file_path);
				int num_tab = jtp.getTabCount();
				jtp.setSelectedIndex(num_tab-1);
				try
				{
					int ch;
					String str_open="";
					FileInputStream fis = new FileInputStream(opened_file);
					while((ch=fis.read())!=-1)
					{
						if(ch!=13)			
							str_open+=(char)ch;
					}
					txt.setText(str_open);
					txt.setCaretPosition(0);
					vContentAdd(txt);
				}
				catch(Exception e1)
				{
					System.out.print("Exception at fopen-->FileInputStream");
				}
			}
		}
		else
		{
			System.out.print("OpenDialog ---> Cancel");
		}
	}
	
	public void fsaveas()
	{
		JFileChooser saveas = new JFileChooser();
		saveas.addChoosableFileFilter(filter);
		int check_open=0;
		if(vpath.size()!=0)
			 check_open = saveas.showSaveDialog(jtp);
		String file_name="";
		String full_file_path="";
		File file_itself = saveas.getSelectedFile();
		try
		{
			if(check_open==JFileChooser.APPROVE_OPTION)
			{
				 if(!(file_itself.isFile()))
				 {
					int tab_index = jtp.getSelectedIndex();
					JTextArea txt = (JTextArea) jtp.getSelectedComponent();
					String current_text = txt.getText();
					file_name=saveas.getSelectedFile().getName();
					full_file_path=saveas.getSelectedFile().getAbsolutePath();
					try
					{
						FileOutputStream fos = new FileOutputStream(full_file_path);
						int ch=0;
						for(int i=0;i<current_text.length();i++)
						{
								ch=(int)current_text.charAt(i);
								if(ch==10)
									fos.write(13);
								fos.write(ch);
								//System.out.println(ch);
								
						}
						jtp.setTitleAt(tab_index,file_name);
						vpath.set(tab_index,full_file_path);
						vContentUpdate();
					}
					
					catch(Exception e1)
					{
						System.out.print("Exception occured at fsaveas--->fos");
					}	
				 }
				 else
				 {
					 System.out.print("File exists already");
					 int result = JOptionPane.showConfirmDialog(saveas, "File Already Exists.Want to replace?", "Alert!!", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
					 if(result==JOptionPane.NO_OPTION||result==JOptionPane.CLOSED_OPTION)					// if no , function will run again and if yes, then overwrite.
					{
							fsaveas();
					}
					if(result==JOptionPane.YES_OPTION)
					{
						int tab_index = jtp.getSelectedIndex();
						JTextArea txt = (JTextArea) jtp.getSelectedComponent();
						String current_text = txt.getText();
						file_name=saveas.getSelectedFile().getName();
						full_file_path=saveas.getSelectedFile().getAbsolutePath();
						try
						{
							FileOutputStream fos = new FileOutputStream(full_file_path);
							int ch;
							for(int i=0;i<current_text.length();i++)
							{
								ch=(int)current_text.charAt(i);
								if(ch==10)
									fos.write(13);
								fos.write(ch);
							}
							jtp.setTitleAt(tab_index,file_name);
							vpath.set(tab_index,full_file_path);
							vContentUpdate();
						}
						
						catch(Exception e1)
						{
							System.out.print("Exception occured at fsaveas--->fos");
						}						
					}
					
				 }						
			}	
		}
		catch(Exception e1)
		{
			System.out.print("Exception at fsaveas"); // most likely occured at saveas dialog.
		}
	}
	
	public String addFilter(String str)
	{
		int ch=0;
		boolean t=false;
		for(int i=0;i<str.length();i++)
		{
			ch = (int)str.charAt(i);
			if(ch==46)		// . = 46 ascii
			{
				t=true;
				break;
			}
			t=false;
		}
		if(t)
			return str;
		else
		{
			System.out.println(str+".java");
			return str+".java";
		}
	}
	
	public void fnew()
	{
			JFileChooser jnew = new JFileChooser();
			jnew.addChoosableFileFilter(filter);
			String file_name,full_file_path,file_name2;
			jnew.setApproveButtonText("New");
			jnew.setDialogTitle("Create New File");
			int check = jnew.showDialog(jtp,"New");
			if(check==JFileChooser.APPROVE_OPTION)
			{
				try
				{
					file_name=jnew.getSelectedFile().getName();
					file_name2 = addFilter(file_name);
					full_file_path=jnew.getSelectedFile().getAbsolutePath();
					if(!(file_name.equals(file_name2)))
					{
						full_file_path = full_file_path.replaceAll(file_name,file_name2);
						file_name = file_name2;						
					}
					File directory = new File(full_file_path);
					
					System.out.println("Full file path = "+full_file_path);
					
					
					if(directory.createNewFile())							// returns false if file with same name exists
					{
						FileOutputStream fos = new FileOutputStream(full_file_path);
					
						JTextArea txt = new JTextArea();
						if(isTabOpen(full_file_path))
						{
							jtp.setSelectedIndex(active_tab);
						}
						else
						{
							jtp.addTab(file_name,txt);
							vpath.add(full_file_path);
							vContentAdd(txt);
							jtp.setSelectedIndex(jtp.getTabCount()-1);
						}
					}
					else
					{
						System.out.print("File with same name exists");
						int result = JOptionPane.showConfirmDialog(jtp, "File Already Exists.Want to replace?", "Alert!!", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
						if(result==JOptionPane.YES_OPTION)
						{
							full_file_path=jnew.getSelectedFile().getAbsolutePath();
							FileOutputStream fos = new FileOutputStream(full_file_path);
					
							JTextArea txt = new JTextArea();
								if(isTabOpen(full_file_path))
							{
								jtp.setSelectedIndex(active_tab);
								JTextArea jtxt = (JTextArea)jtp.getSelectedComponent();
								jtxt.setText("");
								
							}
							else
							{
								jtp.addTab(file_name,txt);
								vpath.add(full_file_path);
								vContentAdd(txt);
								jtp.setSelectedIndex(jtp.getTabCount()-1);
							}
						}
						else
							if(result==JOptionPane.NO_OPTION||result==JOptionPane.CLOSED_OPTION)
							{
								fnew();
							}
						
							
					}
				}
				catch(Exception e1)
				{
					System.out.print("Exception at new_dialog->new");
				}
				
			}
			else
			{
				System.out.print("newdialog -> Cancel clicked");
			}
	}
		
	public boolean isTabOpen (String path)					// if tab is already opened , true else false.
	{
		String oldpath="";
		boolean answer=false;
		if(vpath.size()==0)
		{
			active_tab=0;									
			return false;
		}
		for(int i =0;i<vpath.size();i++)
		{
			oldpath = (String)vpath.get(i);
			if(path.equals(oldpath))
			{
				answer=true;
				//System.out.print("Active tab="+i);
				active_tab = i;
			}
		}
		return answer;
	}
	public void fsave()
	{
		if(vpath.size()!=0)
		{
			int jtp_index = jtp.getSelectedIndex();
			Object obj = jtp.getSelectedComponent();
			JTextArea txt = (JTextArea) obj;
			String ta_content = txt.getText();
			String file_path = (String)vpath.elementAt(jtp_index);
			try
			{
				FileOutputStream fos = new FileOutputStream(file_path);
				int ch;
				for(int i=0;i<ta_content.length();i++)
				{
					ch=(int)ta_content.charAt(i);
					if(ch==10)
						fos.write(13);
					fos.write(ch);
				}		
				
				vContentUpdate();
			}
			catch(Exception e1)
			{
				System.out.print("Exception at fsave");
			}
		}
	}
	public void vContentAdd(JTextArea txt)				// in open and new
	{
		/*System.out.println("v Content add is called");
		int jtp_index = jtp.getSelectedIndex();
		Object obj = jtp.getComponentAt(jtp.getSelectedIndex());
		System.out.println(jtp_index);
		Object obj = jtp.getSelectedComponent();
		System.out.print(obj);
		*/
		
		//JTextArea Jtxt = (JTextArea) obj;
		
		String ta_content = txt.getText();
		vcontent.add(ta_content);
	}
	
	public void vContentUpdate()		// in save .
	{
		System.out.println("v Content update is called");
		int jtp_index = jtp.getSelectedIndex();
		Object obj = jtp.getSelectedComponent();
		JTextArea txt = (JTextArea) obj;
		String ta_content = txt.getText();
		vcontent.set(jtp_index,ta_content);
	}
	
	public void fclose()
	{
		if(vpath.size()>0)
		{
			int jtp_index = jtp.getSelectedIndex();
			Object obj = jtp.getSelectedComponent();
			JTextArea txt = (JTextArea) obj;
			String ta_content = txt.getText();						//Content stored in text area right now.
			String vString="";
			vString = (String)vcontent.get(jtp_index);				// Content stored in vector vcontent.
			
			if(vString.equals(ta_content))
			{
				//remove tab
				jtp.remove(jtp_index);
				// remove Vector for both
				vpath.remove(jtp_index);
				vcontent.remove(jtp_index);
			}
			else
			{
				//ask for save.
				int result = JOptionPane.showConfirmDialog(jtp,"Do you want to save??","Alert!!",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(result==JOptionPane.YES_OPTION)
				{
					fsave();
					//remove tab
					jtp.remove(jtp_index);
					// remove Vector for both
					vpath.remove(jtp_index);
					vcontent.remove(jtp_index);
				}
				if(result==JOptionPane.NO_OPTION)
				{
					//remove tab
					jtp.remove(jtp_index);
					// remove Vector for both
					vpath.remove(jtp_index);
					vcontent.remove(jtp_index);
				}
				if(result==JOptionPane.CANCEL_OPTION)
				{
					//Do nothing...
				}
			}		
		}
	}
	
	public static void main(String args[])
	{
		JCreator create = new JCreator();
	}
	
	class Closeall implements ActionListener
	{
		JPanel jp1;
		JButton b1,b2,b3;
		Vector v1;
		
		public Closeall()
		{
			// A frame will pop up which will contain a jlist in it which will have names of all the tabs that are not saved.
		// Frame will also have three buttons in it, save ,discard and cancel. Jlist will also have multiselection feature. 
		//	K
		cframe = new JFrame("Close All");
		cframe.setSize(400,200);
		clistmodel = new DefaultListModel();	// multiple mode is default.
		clist = new JList(clistmodel);			
		
		b1 = new JButton ("Save");
		b2 = new JButton("Discard");
		b3 = new JButton("Cancel");
		
		jp1 = new JPanel();
		jp1.add(b1);
		jp1.add(b2);
		jp1.add(b3);
		
		cframe.add(clist);
		cframe.add(jp1,"South");
		
		updatelist();
		
		//cframe.setVisible(true);
		cframe.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		/*Adding events to buttons*/
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
		}
		
		public void updatelist()
		{
			clistmodel.removeAllElements();
			System.out.println("\nVpath's size = "+vpath.size());
			int numoftab = vpath.size();
			Vector varr = new Vector();
			for(int i=0;i<numoftab;i++)
			{
				if(checkContent(i))
				{
					System.out.println("\n---checkContent true---");
					File f = new File((String)vpath.get(i));
					String file_name = f.getName();
					clistmodel.addElement(file_name);
				}
				else
				{
					varr.add(i);
				}
			}			
			closeit(varr);			
			cframe.repaint();			
			if(clistmodel.getSize()!=0)
				cframe.setVisible(true);		
		}
		
		public void closeit(Vector varr)				// it closes the files whose content is unchanged or saved upto date.
		{
			Vector vremove = new Vector();
			Vector vremove2 = new Vector();
			for(int i=0;i<varr.size();i++)
			{
				vremove.add(vpath.get((int)varr.get(i)));
				vremove2.add(vcontent.get((int)varr.get(i)));
			}
			for(int i = varr.size()-1;i>=0;i--)
			{
				jtp.remove((int)varr.get(i));
			}
			vpath.removeAll(vremove);
			vcontent.removeAll(vremove2);				
		}
		
		private boolean checkContent(int index)						// if content doesn't match,returns true.
		{
			String storedtxt = (String) vcontent.get(index);
			JTextArea currenttxt = (JTextArea) jtp.getComponentAt(index);
			
			String str1 = currenttxt.getText();
			
			//System.out.println("\n vcontent \n"+storedtxt);
			//System.out.println("\n current text in txt area \n"+str1);
			
			if(str1.equals(storedtxt))
				return false;
			else
				return true;
		}
		
		public void getList()
		{
			int arr[] = clist.getSelectedIndices();
			System.out.print("These are selected\n");
			v1 = new Vector();
			for(int i=0;i<arr.length;i++)
			{
				System.out.println(arr[i]);
				v1.add(arr[i]);
			}
		}
		
		public void cSave(Vector v1)
		{
			try
			{
				System.out.println("\nSize of v1="+v1.size());
				for(int i=0;i<v1.size();i++)
				{
					JTextArea currenttxt = (JTextArea) jtp.getComponentAt((int)v1.get(i));
					String ta_content = currenttxt.getText();
					//System.out.println("ta_content:\n"+ta_content);
					String fpath = (String) vpath.get(i);
					//System.out.println("fpath :\n"+fpath);
					FileOutputStream fos = new FileOutputStream(fpath);
					int ch;
					for(int j=0;j<ta_content.length();j++)
					{
						ch=(int)ta_content.charAt(j);
						if(ch==10)
							fos.write(13);
						fos.write(ch);
					}				
					vContentUpdate2((int)v1.get(i),ta_content);
				}
				discardAll();
			}
			catch(Exception e1)
			{
				System.out.println(e1.getMessage()+"\t Exception occured at cSave");
				e1.printStackTrace();
			}
		}
		
		public void vContentUpdate2(int index,String str)
		{
			vcontent.set(index,str);
		}
		
		public void discardAll()
		{
			vpath.removeAllElements();
			vcontent.removeAllElements();
			jtp.removeAll();
			cframe.setVisible(false);
			System.out.println("tab count :"+jtp.getTabCount());
			if(exitSystem)
			{
				System.exit(1);
			}
		}
		
		public void actionPerformed(ActionEvent e)
		{
			Object ae = e.getSource();
			
			if(ae==b1)		//Save
			{
				System.out.println("Save is clicked");
				getList();			//Value will go in Instance variable v1.
				cSave(v1);
			}
			if(ae==b2)		//Discard
			{
				System.out.println("discard is clicked");
				discardAll();
			}
			if(ae==b3)		//Cancel
			{
				System.out.println("Cancel is clicked");
				cframe.setVisible(false);
			}
		}	
	}
	
	class FindDialog extends JDialog implements ActionListener,WindowListener
	{		
		JButton b1,b2;
		JLabel l1;
		JTextField tf1;
		JPanel p1,p2;
		String currenttab;		// Current vpath in this.
		int currentposition;	// Current caret position in this.
		JTextArea currentTxt;	//Current TextArea
		String currenttf;		// TextField stored in firstrun.
		boolean firstrun=false;
		
		public FindDialog()
		{
			super(f,"Find Dialog", Dialog.ModalityType.MODELESS);
			setSize(200,120);
			b1 = new JButton("Find Next");
			b2 = new JButton("Cancel");
			l1 = new JLabel("Find :");
			tf1 = new JTextField(15);
			p1 = new JPanel(new GridLayout(2,0));
			p2 = new JPanel();
			
			p1.add(l1);
			p1.add(tf1);
			
			p2.add(b1);
			p2.add(b2);
			
			add(p1,BorderLayout.CENTER);
			add(p2,BorderLayout.SOUTH);
			setVisible(true);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			//setAlwaysOnTop(true);
			
			/*Adding action/window listeners*/
			
			b1.addActionListener(this);
			b2.addActionListener(this);
			tf1.addActionListener(this);
			addWindowListener(this);
			
			/* First run defaults */
			firstrun = true;
			
			//this.toFront();			
		}
		public void actionPerformed(ActionEvent e)
		{
			Object ae = e.getSource();			
			if(ae==b1)
			{
				System.out.println("Find is clicked");
				fFind();
			}
			else
				if(ae==b2)
				{
					System.out.println("Close is clicked");
					tf1.setText("");
					this.dispose();
				}			
		}
		
		public void fFind()
		{
			int jtp_index = jtp.getSelectedIndex();
			String tabchange = (String) vpath.get(jtp_index);
			String tfchange = tf1.getText();
			isFind = true;
			if(tfchange.length()!=0)
			{	
				if(firstrun || !(currentTxt.getCaretPosition()==currentposition)||!(currenttab.equals(tabchange))||!(currenttf.equals(tfchange)))			// Clicking find for the first time || other conditions
				{
					//if(firstrun==false)
						//System.out.println("Hello\n"+currentTxt.getCaretPosition()+"\t"+currentposition+"\n"+currenttf+"\t"+tfchange);
					
					currentTxt = (JTextArea) jtp.getSelectedComponent();
					fpattern = Pattern.compile(Pattern.quote(tf1.getText()));
					fmatcher = fpattern.matcher(currentTxt.getText());
					currentposition = currentTxt.getCaretPosition();

					jtp_index = jtp.getSelectedIndex();
					currenttab = (String) vpath.get(jtp_index);			
					currenttf = tf1.getText();
					
					if((fmatcher.find(currentposition)))
					{
						fmatcher.find(currentposition);
						if(firstrun==true)
							currentTxt.requestFocus();			
						currentTxt.select(fmatcher.start(),fmatcher.end());
						currentposition = currentTxt.getCaretPosition();
						firstrun=false;
						//this.toFront();
					}
					else
					{
						System.out.println("No match found!");
						JOptionPane.showMessageDialog(this, "No Match Found", "Alert!!", JOptionPane.ERROR_MESSAGE);
						isFind = false;
					}
				}
				else
				{
					//System.out.println("Else Part\n"+currentTxt.getCaretPosition()+"\t"+currentposition+"\n"+currenttf+"\t"+tfchange);
					if(fmatcher.find())
						currentTxt.select(fmatcher.start(),fmatcher.end());	
					else
					{
						System.out.println("No match found");
						JOptionPane.showMessageDialog(this, "No Match Found", "Alert!!", JOptionPane.ERROR_MESSAGE);
						isFind = false;
					}				
					currentposition = currentTxt.getCaretPosition();	
				}
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Find box can't be empty!", "Alert!!", JOptionPane.ERROR_MESSAGE);
				isFind = false;
			}
		}
		
		public void setActive(boolean b)
		{
			this.setVisible(b);
		}
		
		
		public void windowActivated(WindowEvent e){}
		public void windowClosed(WindowEvent e){}
		public void windowDeactivated(WindowEvent e){}
		public void windowDeiconified(WindowEvent e){}
		public void windowIconified(WindowEvent e){}
		public void windowOpened(WindowEvent e){}
		
		public void windowClosing(WindowEvent e)
		{
			System.out.println("windowClosing called");
			tf1.setText("");
		}
	}
	
	class ReplaceDialog extends FindDialog
	{
		JButton b3,b4;
		JPanel p3,p4;
		JTextField replacetf;
		JLabel replacelabel;
		public ReplaceDialog()
		{
				remove(p2);	
				setSize(250,190);
				b3 = new JButton("Replace");
				b4 = new JButton("Replace All");
				
				p3 = new JPanel(new GridLayout(2,2));
				
				p3.add(b1);
				p3.add(b3);
				p3.add(b4);	
				p3.add(b2);
				
				Panel p4 = new Panel();
				replacelabel = new JLabel("Replace With :");
				replacetf = new JTextField(15);
				p4.add(p1);
				
				JPanel ptemp = new JPanel(new GridLayout(2,1));
				ptemp.add(replacelabel);
				ptemp.add(replacetf);
				p4.add(ptemp);
				
				
				add(p4,BorderLayout.CENTER);
				p4.repaint();
				add(p3,BorderLayout.SOUTH);
				//p3.repaint();
				
				/*Adding Listeners*/
				b3.addActionListener(this);
				b4.addActionListener(this);			
				
				setVisible(true);
				setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
		
		public void actionPerformed(ActionEvent e)
		{
			//String str = e.getActionCommand();
			Object ae = e.getSource();
			//System.out.println(str+"was clickedd");
			
			if(ae==b1)
			{
				System.out.println("Find Next is clicked!!");
				fFind();
			}
			else
				if(ae==b2)
				{
					System.out.println("Close");
					replacetf.setText("");
					tf1.setText("");
					this.dispose();
				}
			else
				if(ae==b3)
				{
					System.out.println("Replace");
					fReplace();
					
				}
			else
				if(ae==b4)
				{
					System.out.println("Replace All");
					fReplaceAll();
				}
				
		}
		
		public void fReplace()
		{
			if(firstrun==false)
			{
				String find_text = tf1.getText();
				String compare = currentTxt.getSelectedText();
				//System.out.println("\nTextField :"+find_text+"\t"+compare);
				String replace_text;
				if(find_text.equals(compare))
				{
					System.out.print("\nText Replace initiated");
					replace_text = replacetf.getText();
					currentTxt.replaceSelection(replace_text);
				}
			}
			try			//this try is Not doing anything and can be removed.
			{
				if(fmatcher.hitEnd())
					System.out.print("Hit the End");
			}
			catch(Exception e1)
			{}
			
			
			fFind();				
		}
		
		public void fReplaceAll()
		{
			fFind();
			if(isFind)
			{
				String replace_text = replacetf.getText();
				try
				{
					String str = fmatcher.replaceAll(replace_text);
					currentTxt.setText(str);
				}
				catch(Exception e1)
				{
					System.out.print("Exception in replace all");
				}
			}
		}
		
	}
}
	
	
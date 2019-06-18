package HighlightMaker;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;

public class HighlightMakerFrame extends JFrame{
	private String URL = new String();		//to store URL user entered
	private String FileName = "";		//to store fileName
	private String VODID = "";		//to store VODID
	private final int Origin = 0;	//number to distinct quality
	private final int High = 1;     //number to distinct quality
	private final int Low = 3;      //number to distinct quality
	private final int Audio = 2;    //number to distinct quality
	private int PQValue = Low;		//initial number of  quality
	private String targetString;    //to store target string want to search
	private int timeRange;			//to store time range
	private int minTimes;			//to store over how many times will analysis as highlight
	private String endText = "";    //to store end text which show that what's is end now
	private static ArrayList<Time> timeArray;             //to store initial analysis highlight time
	private static ArrayList<HighLightTime> highLight;    //to store target highlighttime
	private String whichMode;       //to store number of dinstinct which analysis mode to use
	private ArrayList<String> sourceFilePath = new ArrayList<String>();//to stroe soucepath used to combine video
	private downloadInfo downloader;//object need to call while download vedio
	private TwitchChatFetcher TCF;  //object need to call while download chat
	private ChatToTime CTT;         //object need to call while analysis chat
	
	private JLabel VODMsg;				//to tell user to enter VOD ID
	private JTextField toTextVODID;		//to catch VOD ID
	private JButton VODDownlod;			//button to download chat
	private JPanel panelForEnterVODID;  //panel for second row
	private JLabel FileMsg;		//to tell user what to enter (fileName)
	private JTextField toTextFileName;	//to catch fileName
	private JButton btnRun;		//click to run the program
	private JLabel PQMsg;		//to show message about picture quality
	private ButtonGroup PQ;		//Button Group to union the picture quality
	private JPanel	PQPanel;	//where four radio button to be put
	private JRadioButton originButton;		//720p
	private JRadioButton highButton;		//360p
	private JRadioButton lowButton;		//160p
	private JRadioButton audioButton;		//only audio
	private JLabel ModeMsg;
	private ButtonGroup Mode;		//to union the ways to  make highlight video 
	private JPanel ModePanel;		//where three radio button to be put
	private JPanel VODPanel;
	private JTextField enterDefaltAdvance;
	private JRadioButton byKeyword;		//user enter the keyword
	private JRadioButton byFrequency;		//making video by high frequency
	private JRadioButton bySD;		//making video by standard deviation
	private JScrollPane endPane;
	private JTextArea endPrint;
	private JPanel panelForTargetString;
	private JPanel panelForTimeRange;
	private JPanel panelForMinTimes;
	private JLabel textTargetString;
	private JLabel textTimeRange;
	private JLabel textMinTimes;
	private JTextField enterTargetString;
	private JTextField enterTimeRange;
	private JTextField enterMinTimes;
	private JButton analysis;
	private JButton combineBtn;
	private JPanel panelForChangeHighlight;
	private JComboBox<String> highlightComboBox;
	private JPanel panelForADText;
	private JTextField enterAdvance;
	private JTextField enterDelay;
	private JLabel advanceText;
	private JLabel delayText;
	private JPanel panelForAdjustBtn;
	private JButton changeHighlightBtn;
	private JButton downloadOneBtn;
	private JPanel comboBoxPanel;
	private JButton deleteBtn;
	private JButton addIntro;
	private JLabel introText;
	private Path introPath;
	private String introPathString;
	private JButton upLoad;
	
	public HighlightMakerFrame() {
		super("Video Highlight Maker");		//name window
		initComponents();		//initial the components
		super.setSize(600, 600);		//set window size
		this.setLocationRelativeTo(null);
		this.setLayout(new GridLayout(17, 1));
	}
	
	private void initComponents() {
		//first row object
		VODMsg = new JLabel("Please enter VOD ID: "); 	//set label message
		//second row objects
		toTextVODID = new JTextField("");		      	//set text field
		
		VODDownlod = new JButton();                   		//button click to download chat
		VODDownlod.setText("Chat Download");		  		//set button name
		VODDownlodHandler VODhr = new VODDownlodHandler();	//set button action listener
		VODDownlod.addActionListener(VODhr);				//add button action listener
		
		panelForEnterVODID = new JPanel(new BorderLayout());		//panel for second row
		panelForEnterVODID.add(toTextVODID,BorderLayout.CENTER);	//add textfiled in panel
		panelForEnterVODID.add(VODDownlod,BorderLayout.EAST);		//add button in panel
		
		//third row object
		FileMsg = new JLabel("Please enter FileName:");		//set label message
		
		//forth row object
		toTextFileName = new JTextField("");		  	//set text field
		
		//fifth row object
		ModeMsg = new JLabel("Please choose highlight mode: ");
		
		//sixth row object
		Mode = new ButtonGroup();	//mode radiobutton group
		ModePanel = new JPanel(new FlowLayout());	//panel for sixth panel
		byKeyword = new JRadioButton("By Keyword", false);		//radio button to choose analysis mode
		byFrequency = new JRadioButton("By Frequency", false);	//radio button to choose analysis mode
		bySD = new JRadioButton("By Standar Deviation", false);	//radio button to choose analysis mode
		Mode.add(byKeyword);
		Mode.add(byFrequency);
		Mode.add(bySD);
		enterDefaltAdvance = new JTextField("delay",3);			//textfiled to adjust stream delay
		analysis = new JButton();								//button press to run analysis
		analysis.setText("Analysis");
		AnalysisHandler anahr = new AnalysisHandler();
		analysis.addActionListener(anahr);
		ModePanel.add(byKeyword);
		ModePanel.add(byFrequency);
		ModePanel.add(bySD);
		ModePanel.add(enterDefaltAdvance);
		ModePanel.add(analysis);
		//add ItemListener
		byKeyword.addItemListener(new ModeHandler());
		byFrequency.addItemListener(new ModeHandler());
		bySD.addItemListener(new ModeHandler());
		
		//seventh row objects
		panelForTargetString = new JPanel(new BorderLayout());
		textTargetString = new JLabel("Enter target string:");
		enterTargetString = new JTextField();		//textfiled to enter target string
		panelForTargetString.add(textTargetString,BorderLayout.WEST);
		panelForTargetString.add(enterTargetString,BorderLayout.CENTER);
		
		//eighth row objects
		panelForTimeRange = new JPanel(new BorderLayout());
		textTimeRange = new JLabel("Enter time range:   ");
		enterTimeRange = new JTextField("");		//textfiled to enter time range
		panelForTimeRange.add(textTimeRange,BorderLayout.WEST);
		panelForTimeRange.add(enterTimeRange,BorderLayout.CENTER);
		
		//ninth row objects
		panelForMinTimes = new JPanel(new BorderLayout());
		textMinTimes = new JLabel("Enter min times:     ");
		enterMinTimes = new JTextField("");			//textfiled to enter minimum times
		panelForMinTimes.add(textMinTimes,BorderLayout.WEST);
		panelForMinTimes.add(enterMinTimes,BorderLayout.CENTER);
		
		//tenth row object
		PQMsg = new JLabel("Please choose the Picture Quality: ");
		
		//eleventh row objects
		PQ = new ButtonGroup();
		PQPanel = new JPanel();
		originButton = new JRadioButton("Origin", false);	//radiobuttons to choose quality of video
		highButton = new JRadioButton("High", false);
		lowButton = new JRadioButton("Low", true);
		audioButton = new JRadioButton("Audio", false);
		PQ.add(originButton);
		PQ.add(highButton);
		PQ.add(lowButton);
		PQ.add(audioButton);
		addIntro = new JButton();
		addIntro.setText("add intro");
		introText = new JLabel("(1080p only)");
		PQPanel.add(originButton);
		PQPanel.add(highButton);
		PQPanel.add(lowButton);
		PQPanel.add(audioButton);
		PQPanel.add(addIntro, BorderLayout.EAST);
		PQPanel.add(introText, BorderLayout.EAST);
		addIntro.setVisible(false);
		introText.setVisible(false);
		//add ItemListener
		originButton.addItemListener(new PQHandler(Origin));
		highButton.addItemListener(new PQHandler(High));
		lowButton.addItemListener(new PQHandler(Low));
		audioButton.addItemListener(new PQHandler(Audio));
		
		addIntro.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				introPath = getFilePath();
				if(introPath!=null && Files.exists(introPath)) {
					introPathString = introPath.toAbsolutePath().toString();
					String check[] = introPathString.split("\\.");
					try {
						if(check[1].equals("mp4")) {
							endText += "chosen file is: " + introPath.toAbsolutePath().toString() + System.lineSeparator();
							endPrint.setText(endText);
							System.out.println("type correct");
						}
						else {
							introPath = null;
							endText += "chosen intro type is not mp4, intro would not be added to video" + System.lineSeparator();
							endPrint.setText(endText);
						}
					} catch(ArrayIndexOutOfBoundsException e1) {
						endText += "chosen intro type is not mp4, intro would not be added to video" + System.lineSeparator();
						endPrint.setText(endText);
						e1.printStackTrace();
					}
				}
				else {
					endText += "did not choose any file" + System.lineSeparator();
					endPrint.setText(endText);
				}
			}			
		});
		
		//twelfth row objects
		this.btnRun= new JButton();			//button to download all highlight vedio
	    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    btnRun.setText("Download");
	    btnRun.addActionListener(new ActionListener() {
	      @Override
	    	public void actionPerformed(ActionEvent evt) {	    		
	    		setFileName(toTextFileName.getText());
	    		System.out.println("FileName: " + FileName);
	    		setVODID(toTextVODID.getText());
				dealSourcePath();
				checkDir();
	    		try {
					downloader = new downloadInfo(getVODID());
				} catch (IOException e) {					
					e.printStackTrace();
				}
	    		try {
					btnRunActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		upLoad.setVisible(true);
	      }
	    });
		
		//thirteenth row objects
	    comboBoxPanel = new JPanel();
	    deleteBtn = new JButton();
	    deleteBtn.setText("Delete");
	    DeleteBtnHandler delBtnhr = new DeleteBtnHandler();
	    deleteBtn.addActionListener(delBtnhr);
		highlightComboBox = new JComboBox<String>();		//combobox to select time want to adjust
		highlightComboBox.setMaximumRowCount(5);
		HighlightBoxHandler hd = new HighlightBoxHandler();
		highlightComboBox.addItemListener(hd);
		comboBoxPanel.add(highlightComboBox);
		comboBoxPanel.add(deleteBtn);
		
		panelForADText = new JPanel(new GridLayout(2,2));
		advanceText = new JLabel("enter time to adjust start time:");
		delayText = new JLabel("enter time to adjust end time:");
		enterAdvance = new JTextField("",4);	//textfiled to enter how many seconds want to advance
		enterDelay = new JTextField("",4);		//textfiled to enter how many seconds want to delay
		panelForADText.add(advanceText);
		panelForADText.add(enterAdvance);
		panelForADText.add(delayText);
		panelForADText.add(enterDelay);
		
		panelForAdjustBtn = new JPanel(new GridLayout(1,2));
		changeHighlightBtn = new JButton();	//button press to adjust selected time
		changeHighlightBtn.setText("Adjust");
		ChangeHighlightBtnHandler CHBhr = new ChangeHighlightBtnHandler();
		changeHighlightBtn.addActionListener(CHBhr);
		changeHighlightBtn.setPreferredSize(new Dimension(80, 40));
		
		downloadOneBtn = new JButton();		//button press to download selected time
		downloadOneBtn.setText("<html>Download" + "<br>" + "this</html>");
		DownloadOneBtnHandler DLOBhr = new DownloadOneBtnHandler();
		downloadOneBtn.addActionListener(DLOBhr);
		downloadOneBtn.setPreferredSize(new Dimension(80, 40));
		
		panelForAdjustBtn.add(changeHighlightBtn);
		panelForAdjustBtn.add(downloadOneBtn);
		
		panelForChangeHighlight = new JPanel(new BorderLayout());
		panelForChangeHighlight.add(comboBoxPanel,BorderLayout.WEST);
		panelForChangeHighlight.add(panelForADText,BorderLayout.CENTER);
		panelForChangeHighlight.add(panelForAdjustBtn,BorderLayout.EAST);

		//fourteenth row object
		combineBtn = new JButton();		//button press to combine all download video
	    combineBtn.setText("Combine");
	    CombineBtnHandler combinehr = new CombineBtnHandler();
	    combineBtn.addActionListener(combinehr);
		
		//fifteenth row object 
		endPrint = new JTextArea();		//text of end meg
		endPane = new JScrollPane(endPrint);
	    
		//set Frame
	    add(VODMsg);
		add(panelForEnterVODID);
		add(FileMsg);
		add(toTextFileName);		
		add(ModeMsg);
		add(ModePanel);
		add(panelForTargetString);
		add(panelForTimeRange);
		add(panelForMinTimes);
		add(PQMsg);
		add(PQPanel);
	    add(this.btnRun, BorderLayout.SOUTH);
	    add(panelForChangeHighlight);
	    add(combineBtn);
	    
	    
	    upLoad = new JButton();
	    upLoad.setText("up load");
	    add(upLoad, BorderLayout.SOUTH);
	    upLoad.setVisible(false);
	    
	    add(endPane);
	    
	    upLoad.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent evt) {
	    		try {
					uploadVideo();
					endText += "upload setting completed!!!" + System.lineSeparator();
					endPrint.setText(endText);
				} catch (GeneralSecurityException | IOException e) {
					e.printStackTrace();
				}
	    	}
	    });
	    
	    //initial Frame objects
	    highlightComboBox.setVisible(false);
	    enterTargetString.setEditable(false);
		enterTimeRange.setEditable(false);
		enterMinTimes.setEditable(false);
		enterAdvance.setEditable(false);
		enterDelay.setEditable(false);
		endPrint.setEditable(false);
		
	    pack();
	}

	public void chatDownload() throws IOException
	{
		TCF = new TwitchChatFetcher(toTextVODID.getText());
		TCF.chatDataFetch();
		TCF.printData();
	}
	
	public void setURL(String URL) {		//to set URL (from text field)
		this.URL = URL;
	}
	
	public void setVODID(String VODID) {		//to set VOD ID(from text field)
		this.VODID = VODID;
	}
	
	public String getVODID() {		//to get VOD ID(use as method argument)
		return this.VODID;
	}
	
	public void setFileName(String FileName) {		//to set file name
		this.FileName = FileName;
	}
	
	public void setPQ(int PQ) {
		this.PQValue = PQ;
	}
	
	public int getPQ(){
		return this.PQValue;
	}
	
	private class VODDownlodHandler implements ActionListener {
        // handle button event
        @Override
        public void actionPerformed(ActionEvent event) {
        	try
			{
				chatDownload();
				endText += "chat download complete " + System.lineSeparator();
				endPrint.setText(endText);
			} 		
			catch(IOException e)
			{
				JOptionPane.showMessageDialog(null, "Can not find VOD");
			}
        }
    }
	private class AnalysisHandler implements ActionListener {
        // handle button event
        @Override
        public void actionPerformed(ActionEvent event) {
        	analysisChat();
			endText += "analysis complete." + System.lineSeparator();
			endPrint.setText(endText);
			System.out.print(endText);
        }
    }
	private class PQHandler implements ItemListener{
		private int PQ;
		public PQHandler(int value) {
			this.PQ = value;
		}
		@Override
		public void itemStateChanged(ItemEvent event) {
			setPQ(PQ);
			if(PQ == Origin) {
				addIntro.setVisible(true);
				introText.setVisible(true);
				System.out.println("add Intro btn");
			}
			else
				try {
					addIntro.setVisible(false);;
					introPathString = null;
				} catch(Exception e) {
					
					e.printStackTrace();
				}
			System.out.printf("PQ value: %d%n", getPQ());
		}
	}
	private class ModeHandler implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent event) {
			for (Enumeration<AbstractButton> buttons = Mode.getElements(); buttons.hasMoreElements();) {
				AbstractButton button = buttons.nextElement();
				if (button.isSelected()) {
					whichMode = button.getText();
				}
			}
			if(whichMode.equals("By Keyword"))
			{
				enterTargetString.setEditable(true);
				enterTimeRange.setEditable(true);
				enterMinTimes.setEditable(true);
			}
			else if(whichMode.equals("By Frequency"))
			{
				enterTargetString.setEditable(false);
				enterTimeRange.setEditable(true);
				enterMinTimes.setEditable(true);
			}
			else if(whichMode.equals("By Standar Deviation"))
			{
				enterTargetString.setEditable(false);
				enterTimeRange.setEditable(false);
				enterMinTimes.setEditable(false);
			}	
		}
	}
	private class DeleteBtnHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
        	int i = highlightComboBox.getSelectedIndex();
			fileDelete(sourceFilePath.get(i));
			highLight.remove(i);
			timeArray.remove(i);
			sourceFilePath.remove(i);
			updateComboBox();
        }
    }
	private class ChangeHighlightBtnHandler implements ActionListener {
        // handle button event
        @Override
        public void actionPerformed(ActionEvent event) {
        	int i = highlightComboBox.getSelectedIndex();
			fileDelete(sourceFilePath.get(i));
			highLight.get(i).setStartTime(-1 * Integer.parseInt(enterAdvance.getText()));
			highLight.get(i).setEndTime(Integer.parseInt(enterDelay.getText()));
			dealSourcePath(i);
			updateComboBox();
        }
    }
	private class CombineBtnHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
        	dealSourcePath();
			try {
				System.out.printf("%n%n%nGo combine");
				endText += FFMpegUtils.combine(sourceFilePath, FileName);
				endPrint.setText(endText);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
        }
    }
	private class DownloadOneBtnHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
        	int i = highlightComboBox.getSelectedIndex();
			URL = downloader.getResolutionURL(getPQ());
			String now;
			try {
				FFMpegUtils.download(URL, highLight.get(i).getStartTime().toString(), highLight.get(i).getEndTime().toString(), FileName);
			} catch(IOException e) {
				e.printStackTrace();
			}
			now = "Video segment download";
			endText += now + System.lineSeparator();
			endPrint.setText(endText);
        }
    }
	private void btnRunActionPerformed(ActionEvent evt) throws IOException {
		setURL(downloader.getResolutionURL(getPQ()));

		for(int i = 0;i < highLight.size();i++)	{
			String now;
			FFMpegUtils.download(URL, highLight.get(i).getStartTime().toString(), highLight.get(i).getEndTime().toString(), FileName);
			now = "Video segment download(" +String.valueOf(i+1) + "/" + highLight.size()+ ")";
			endText += now + System.lineSeparator();
			endPrint.setText(endText);
		}
		
		System.out.printf("%n%n%nGo combine");
		endText += FFMpegUtils.combine(sourceFilePath, FileName);
		endPrint.setText(endText);
	}
	public void analysisChat()
	{
		System.out.printf("%n%n");
		CTT = new ChatToTime();
		highLight = new ArrayList<HighLightTime>();
		if(whichMode.equals("By Keyword"))
		{
			System.out.printf("test1%n%n");
			targetString = enterTargetString.getText();
			timeRange = Integer.parseInt(enterTimeRange.getText());
			minTimes = Integer.parseInt(enterMinTimes.getText());
			System.out.printf("test2%n%n");
			timeArray = CTT.searchByString(TCF.getArrayListForTime(),TCF.getArrayListForComments(),targetString,timeRange,minTimes);
			System.out.printf("test3%n%n");
		}
		else if(whichMode.equals("By Frequency"))
		{
			timeRange = Integer.parseInt(enterTimeRange.getText());
			minTimes = Integer.parseInt(enterMinTimes.getText());
			timeArray = CTT.searchByFrequency(TCF.getArrayListForTime(),TCF.getArrayListForComments(),timeRange,minTimes);
			System.out.printf("test3%n%n");
		}
		else if(whichMode.equals("By Standar Deviation"))
			timeArray = CTT.searchBySD(TCF.getArrayListForTime(),TCF.getArrayListForComments());
		System.out.printf("size= %d", timeArray.size());
		int advance;
		boolean hasNum = false;
		try {
			advance = Integer.parseInt(enterDefaltAdvance.getText());
			for(int i = 0; i < timeArray.size(); i++)
			{
				highLight.add(new HighLightTime(timeArray.get(i),20 + advance,20));
				System.out.printf("%s%n", highLight.get(i));
			}
			hasNum = true;
		} catch(Exception e) {
			System.out.println(e);
		}
		if(!hasNum)
		{
			for(int i = 0; i < timeArray.size(); i++)
			{
				highLight.add(new HighLightTime(timeArray.get(i)));
				System.out.printf("%s%n", highLight.get(i));
			}
		}
		
		updateComboBox();
		highlightComboBox.setVisible(true);
	};
	public void updateComboBox()
	{
		ArrayList<String> highlightStrs = new ArrayList<String>();
		for(int i = 0; i < timeArray.size(); i++)
		{
			highlightStrs.add(highLight.get(i).toString());
		}
		highlightComboBox.removeAllItems();
		for(String i:highlightStrs)
			highlightComboBox.addItem(i);
	}
	public void dealSourcePath() {
		//sourceFilePath.add("output/intro.mp4");	//add intro to video
		sourceFilePath.clear();
		if(introPathString != null)
			sourceFilePath.add(introPathString);
		for(int i = 0;i < highLight.size();i++)	{
			String tmpStart = highLight.get(i).getStartTime().toString();
			String tmpEnd = highLight.get(i).getEndTime().toString();
			String[] startTime = tmpStart.split(":");
			String[] endTime = tmpEnd.split(":");
			
			String myPath = "output/" + FileName +  startTime[0] + startTime[1] + startTime[2] + "TO" + endTime[0] + endTime[1] + endTime[2] + ".mp4";
			this.sourceFilePath.add(myPath);
			System.out.printf("Source file name: %s%n", sourceFilePath.get(i));
		}		
	}
	public void dealSourcePath(int i) {
		String tmpStart = highLight.get(i).getStartTime().toString();
		String tmpEnd = highLight.get(i).getEndTime().toString();
		String[] startTime = tmpStart.split(":");
		String[] endTime = tmpEnd.split(":");
		
		String myPath = "output/" + FileName +  startTime[0] + startTime[1] + startTime[2] + "TO" + endTime[0] + endTime[1] + endTime[2] + ".mp4";
		this.sourceFilePath.set(i, myPath);
		System.out.printf("Source file name: %s%n", sourceFilePath.get(i));
	}
	private class HighlightBoxHandler implements ItemListener
    {
        //set text and icon
        public void itemStateChanged(ItemEvent event)
        {
            if (event.getStateChange() == ItemEvent.SELECTED)
            {
            	enterAdvance.setEditable(true);
            	enterDelay.setEditable(true);
            }
        }
    }
	public void checkDir() {
		String path = "output";
		File dir = new File(path);
		if(dir.exists() == true){
			System.out.println("dirs is exists");
		}else{
			dir.mkdirs();
			System.out.println(" created dirs");
		}
	}
	public void fileDelete(String path){
		try{
			File file = new File(path);
			if(file.delete()){
			System.out.println(file.getName() + " is deleted!");
			}else{
			System.out.println("Delete operation is failed.");
			}
			}catch(Exception e) {
			e.printStackTrace();
			}
	}
	
	private Path getFilePath(){
	      // configure dialog allowing selection of a file or directory
	      JFileChooser fileChooser = new JFileChooser();
	      fileChooser.setFileSelectionMode(
	         JFileChooser.FILES_AND_DIRECTORIES);
	      int result = fileChooser.showOpenDialog(this);

	      // if user clicked Cancel button on dialog, return
	      if (result == JFileChooser.CANCEL_OPTION)
	          return null;
	      else
	      // return Path representing the selected file
	    	  return fileChooser.getSelectedFile().toPath();
	   } 
	
	public void uploadVideo() throws GeneralSecurityException, IOException {
	        YouTube youtubeService = ApiExample.getService();
	        
	        // Define the Video object, which will be uploaded as the request body.
	        Video video = new Video();
	        
	        // Add the snippet object property to the Video object.
	        VideoSnippet snippet = new VideoSnippet();
	        snippet.setCategoryId("22");
	        snippet.setDescription("Description of uploaded video.");
	        snippet.setTitle(FileName);
	        video.setSnippet(snippet);
	        
	        // Add the status object property to the Video object.
	        VideoStatus status = new VideoStatus();
	        status.setPrivacyStatus("public");
	        video.setStatus(status);

	        // TODO: For this request to work, you must replace "YOUR_FILE"
	        //       with a pointer to the actual file you are uploading.
	        //       The maximum file size for this operation is 64GB.
	        File mediaFile = new File("output/" + FileName + ".mp4");
	        InputStreamContent mediaContent =
	            new InputStreamContent("video/*",
	                new BufferedInputStream(new FileInputStream(mediaFile)));
	        mediaContent.setLength(mediaFile.length());

	        // Define and execute the API request
	        YouTube.Videos.Insert request = youtubeService.videos()
	            .insert("snippet,status", video, mediaContent);
	        Video response = request.execute();
	        System.out.println(response);
	}
}
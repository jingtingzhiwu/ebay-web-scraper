import java.util.*;
import java.io.*;
import java.net.*;
/* import Jsoup for parsing html */
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/* packages that are need for GUI interface */
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.EventQueue;
/* packages needed to download images */
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
/* packages needed to get current date and time */
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EbayWebScraper extends JFrame {

	public static JTextField searchText;

	public EbayWebScraper() {
		initUI();
	}

/* initialize graphical user interface */
	public void initUI() {
		JFrame frame = new JFrame("eBay Web Scraper");
  		
		frame.setSize(400, 200);
  		frame.setLocationRelativeTo(null);
  		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
  		frame.setVisible(true);
  		
		JPanel panel = new JPanel();
  		frame.add(panel);
  		panel.setLayout(null);
	
		JLabel searchLabel = new JLabel("Search for:");
		searchLabel.setBounds(10, 60, 80, 25);
  		panel.add(searchLabel);
		
/* text field that takes the search parameters */
		searchText = new JTextField(20);
  		searchText.setBounds(90, 60, 300, 25);
  		panel.add(searchText);

		JButton searchButton = new JButton("Search");
  		searchButton.setBounds(150, 130, 100, 30);
  		panel.add(searchButton);

/* call saveSearchResults function when searchButton is pressed */		
		searchButton.addActionListener(new ActionListener() {
   			public void actionPerformed(ActionEvent e) {
				try {
    					saveSearchResults();
				} catch(IOException f) {
					f.printStackTrace();
				}
   			}
  		});
	}

/* function to download auction images */
	public void saveSearchResults() throws IOException {
/* if directory does not exist, create directory to hold all web scraper results */
		File scraperDir = new File("eBay web scraper results");
	
		if(!scraperDir.exists())
                        scraperDir.mkdir();

/* adjust search parameters as needed for ebay URL format */
		String searchTerms  = searchText.getText();
		String searchTermsFix = searchTerms.replace("/", " ");
		searchTermsFix = searchTermsFix.replace(" ", "+");

/* get date and time when search was made */
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
                Calendar calendar = Calendar.getInstance();
                String date = dateFormat.format(calendar.getTime());
	
/* create directory for the current search */
		File searchDir = new File(scraperDir.getAbsolutePath() + '/' + searchTermsFix + ", " + date);
		searchDir.mkdir();

/* counter for the number of auctions found */
		int itemCount = 0;
		try {
/* acquire first 50 auctions that match search parameters  */
			Document doc = Jsoup.connect("http://www.ebay.com/sch/i.html?_nkw=" + searchTermsFix + "&_ipg=50").get();

			Elements auctions = doc.select("a.imgWr2");
			for (Element auction : auctions) {
				itemCount++;
  				String url = auction.attr("href");
/* create individual directory for each individual auction */
				Document doc2 = Jsoup.connect(url).timeout(10*1000).get();
				Element auctionTitle = doc2.select("span#vi-lkhdr-itmTitl").first();
				String auctionTitleText = auctionTitle.text();
				String auctionTitleTextFix = auctionTitleText.replace("/", " ");
				System.out.println(itemCount + ") " + auctionTitleTextFix);
				File auctionDir = new File(searchDir.getAbsolutePath() + '/' + auctionTitleTextFix);
				auctionDir.mkdir();
				
/* if it exists, save main image of auction page to the current auction's directory */
				Element mainImage = doc2.select("img#icImg").first();	
				String mainImgSrc = mainImage.attr("src");
				BufferedImage imageFile = null;
				if(!mainImgSrc.equals("http://ir.ebaystatic.com/pictures/aw/pics/nextGenVit/imgNoImg.gif")) {
					String mainImgSrcFix = mainImgSrc.substring(0, mainImgSrc.length() - 6) + "57" + mainImgSrc.substring(mainImgSrc.length() - 4, mainImgSrc.length());
					URL imgSrcURL = new URL(mainImgSrcFix);
					String imgFilename = mainImgSrc.substring(mainImgSrc.length() - 6, mainImgSrc.length());
					imageFile = ImageIO.read(imgSrcURL);
					ImageIO.write(imageFile, "JPG", new File(auctionDir.getAbsolutePath() + '/' + imgFilename));
				}

/* write the URL of the auction to a text file in the auction's directory */
				String textFilename = new String(auctionDir.getAbsolutePath() + '/' + auctionDir.getName());
                        	File output = new File(textFilename);
                        	FileWriter fw = new FileWriter(output, false);
                        	BufferedWriter bw = new BufferedWriter(fw);
				bw.write(url);
				bw.close();
			}
		} catch(IOException e) {
/* catch exception */
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
    			@Override
    			public void run() {
     				EbayWebScraper scraper = new EbayWebScraper();
    			}
   		});
 	}
}

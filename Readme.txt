A program the extracts the image and URL from Ebay auctions relevant to the user's search parameters, written in Java using the Java Swing GUI toolkit and jsoup html parser library 

The jsoup JAR file can be found here:
http://jsoup.org/download

if you are using Windows compile by entering the following command into the command prompt:
javac -cp .;jsoup-1.8.1.jar EbayWebScraper.java 

To run program in Windows, enter:
java -cp .;jsoup-1.8.1.jar EbayWebScraper

if you are using Linux/Unix, compile by entering:
javac -cp .:jsoup-1.8.1.jar EbayWebScraper.java 

To run the program in Linux/Unix, enter:
java -cp .:jsoup-1.8.1.jar EbayWebScraper
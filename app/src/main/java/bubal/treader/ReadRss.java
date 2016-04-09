package bubal.treader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Fetches RSS feeds data.
 */
public class ReadRss extends AsyncTask<Void, Void, Void> {

    Context context;
    ProgressDialog progressDialog;

    String address = "https://www.sciencemag.org/rss/news_current.xml";
    URL url;

    public ReadRss(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("test");
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
    }


    @Override
    protected Void doInBackground(Void... params) {

        processXml(getData());

        return null;
    }

    private void processXml(Document data) {

        if (data != null) {

            ArrayList<FeedItem> feedItems = new ArrayList<>();

            //store root element
            Element root = data.getDocumentElement();

            //store channel node which is fist child of rss root tag
            Node channel = root.getChildNodes().item(1);

            //store child nodes of channel
            NodeList items = channel.getChildNodes();

            for (int i = 0; i < items.getLength(); i++) {
                Node currentChild = items.item(i);

                if (currentChild.getNodeName().equalsIgnoreCase("item")) {

                    FeedItem item = new FeedItem();
                    NodeList itemChildes = currentChild.getChildNodes();

                    for (int j = 0; j < itemChildes.getLength(); j++) {
                        Node current = itemChildes.item(j);

                        if (current.getNodeName().equalsIgnoreCase("title")) {
                            item.setTitle(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("description")) {
                            item.setDescription(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("pubDate")) {
                            item.setPubDate(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("link")) {
                            item.setLink(current.getTextContent());
                        }
                    }
                    feedItems.add(item);
                    Log.d("itemTitle", item.getTitle());
                    Log.d("itemDescription",item.getDescription());
                    Log.d("itemPubDate",item.getPubDate());
                    Log.d("itemLink",item.getLink());
                }
            }
        }
    }

    /**
     * Parses data from URL.
     */
    public Document getData() {
        try {
            url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(inputStream);

            return xmlDocument;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

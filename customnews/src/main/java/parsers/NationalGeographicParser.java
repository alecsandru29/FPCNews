package parsers;
import com.frontpagenews.models.ArticleModel;
import com.frontpagenews.models.SourceModel;
import com.frontpagenews.repositories.ArticleRepository;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NationalGeographicParser  {
    private ArticleRepository repository;

    public void parseAll() {
        try {
            Document doc = Jsoup.connect("https://www.nationalgeographic.org/news/").get();
            Elements links = doc.select("a[href].ng-image-link");
            for (Element link : links) {
                //System.out.println( link.attr("abs:href"));
                parse(link);
            }
        } catch (IOException e){
            System.out.println (e.toString());
        }
    }

    private void parse(Element link){

        try {
            String article_url = link.attr("abs:href"); //site
            String f_site = article_url;

            Document doc = Jsoup.connect(article_url).get();
            Elements title = doc.select("meta[property=\"og:title\"]");
            String f_id = title.attr("content");
            //System.out.println(f_id);

            Elements content = doc.select("div[itemprop = articleBody]");
            String f_content = content.text();
            //System.out.println(f_content);

            Elements image = doc.select("meta[property=\"og:image\"]");
            String f_image = image.attr("content");
            //System.out.println(f_image);

            Elements tags = doc.select("meta[property=\"article:tag\"]");
            List<String> f_tags = new ArrayList<String>();
            for (Element tag : tags) {
                //System.out.println(tag.attr("content"));
                f_tags.add(tag.attr("content"));
            }

            Elements author = doc.select("p.ng-article-meta span[itemprop=\"author\"]");
            String f_author = author.text();
            //System.out.println(f_author);

            Elements date= doc.select("meta[property=\"article:published_time\"]");
            String d = date.attr("content");
            Date f_date = null;
            try{
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                f_date = df.parse(d);
                //String newDateString = df.format(f_date);
                //System.out.println(newDateString);
                //System.out.println(f_date);

            } catch  (ParseException e) {
                e.printStackTrace();
            }

            SourceModel source = new SourceModel();
            source.setSite(f_site);
            source.setDate(f_date);
            source.setAuthor(f_author);

            ArticleModel article = new ArticleModel();
            article.setTitle(f_id);
            article.setContent(f_content);
            article.setImageUrl(f_image);
            article.setTags(f_tags);
            article.setSource(source);

            repository.save(article);


        }catch (IOException e){
            System.out.println (e.toString());
        }
    }

    public static void main(String[] args){
        NationalGeographicParser p =new NationalGeographicParser();
        p.parseAll();


    }


}

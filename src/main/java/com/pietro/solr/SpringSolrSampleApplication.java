package com.pietro.solr;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pietro.solr.model.Product;
import com.pietro.solr.repository.ProductRepository;

@SpringBootApplication
public class SpringSolrSampleApplication implements CommandLineRunner {
    
    @Autowired
    private ProductRepository repository;

    @Override
    public void run(String... args) throws Exception {

        this.repository.deleteAll();

        // insert some products
        this.repository.save(new Product("1", "Nintendo Entertainment System"));
        this.repository.save(new Product("2", "Sega Megadrive"));
        this.repository.save(new Product("3", "Sony Playstation"));
        
        String urlString = "http://localhost:8983/solr/pietro";
        SolrClient solr = new HttpSolrClient.Builder(urlString).build();       
        
        solr.deleteByQuery("*:*");
        
        long now = System.currentTimeMillis();
        int count = 1000;
        Collection<SolrInputDocument> docs = new ArrayList<>();
        
        for (int i = 4; i < count; i++) {
            // this.repository.save(new Product(String.valueOf(i), "Pietro Pietro"+i));
            
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", String.valueOf(i));
            document.addField("name",  "Pietro"+i);
            
            for (int j = 0; j < 20; j++) {
                document.addField("tax_"+j,  new Integer[]{1, 2, 3, 4, 5});
            }
            
            docs.add(document);
            
            if (i % 200 == 0 || i == count -1) {
                UpdateResponse response = solr.add(docs, 15000);
                docs.clear();
            }
        }
        
        now = System.currentTimeMillis() - now;
        
        System.out.println("Created "+ count + " documents in ms: "+ now);
        
        // fetch all
        System.out.println("Products found by findAll():");
        System.out.println("----------------------------");
        for (Product product : this.repository.findAll()) {
            System.out.println(product);
        }
        System.out.println();

        // fetch a single product
        System.out.println("Products found with findByNameStartingWith('So'):");
        System.out.println("--------------------------------");
        for (Product product : this.repository.findByNameStartingWith("So")) {
            System.out.println(product);
        }
        System.out.println();
    }

	public static void main(String[] args) {
		SpringApplication.run(SpringSolrSampleApplication.class, args);
	}
}

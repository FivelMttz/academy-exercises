package com.predictionmarketing.RecommenderApp;

/**
 * MovieRecommender.java
 * @version 1.0 October 19, 2020
 * @author Adrian Rangel cp
 */ 

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.*;
import java.util.*;
import java.util.zip.*;



public class MovieRecommender {

    // Global variables
    HashMap<String, Integer> uniqueUser = new HashMap<String, Integer>();   // 
    HashMap<String, Integer> uniqueProduct = new HashMap<String, Integer>();
    int totalReviews = 0;


    /**
     * Constructor Method
            * @param args string:filename
     */ 
        public MovieRecommender(String filename) throws IOException{

        //Buffer Reader to read zip filw
        InputStream inputStream = new GZIPInputStream(new FileInputStream(filename));
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String strCurrentLine;

        //Pattern Variables
        String prodPattern = "product/productId: ";
        String userPattern = "review/userId: ";
        String scorePattern = "review/score: ";

        //Auxiliary variables for each line values
        String product = "";
        int productCont = 0;
        String user = "";
        int userCont = 0;
        String score = "";

        //CSV builder
        File moviesData = new File("data/moviesdb.csv");
        FileWriter writer = new FileWriter(moviesData);
        BufferedWriter lineWritter = new BufferedWriter(writer);

            while((strCurrentLine = br.readLine()) != null)
            {

                
                if(strCurrentLine.contains(prodPattern)) {
                    product = strCurrentLine.substring(prodPattern.length());
                    if(!this.uniqueProduct.containsKey(product)){
                        this.uniqueProduct.put(product, productCont);
                        productCont++;
                    }

                }
                if(strCurrentLine.contains(userPattern)) {
                    user = strCurrentLine.substring(userPattern.length());
                    if(!this.uniqueUser.containsKey(user)){
                        this.uniqueUser.put(user, userCont);
                        userCont++;
                    }
                }
                if(strCurrentLine.contains(scorePattern)) {
                    this.totalReviews++;
                    score = strCurrentLine.substring(scorePattern.length());
                    lineWritter.write(this.uniqueUser.get(user) + "," + this.uniqueProduct.get(product) + "," + score + "\n");
                }
                
        }
            lineWritter.close();
            writer.close();
            br.close();
            inputStream.close();


    }
     

       /**
    * Getter. Return hashmap key 
    * @param args int:val
    * @return string: key.
    */
    public String getKey(int val)
    {
        for(String key : this.uniqueProduct.keySet()){
            if(this.uniqueProduct.get(key) == val){
                return key;
            }
        }
        return null;
    }


     /**
    * Mahout Recommender from Example
    * @param args string: user
    * @return recs.
    */
    public List<String> getRecommendationsForUser(String user) throws IOException, TasteException 
    {
        int numUser = this.uniqueUser.get(user);
        List<String> recs = new ArrayList<String>();

        DataModel model = new FileDataModel(new File("data/moviesdb.csv"));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        List<RecommendedItem> recommendations = recommender.recommend(numUser, 3);
        for (RecommendedItem recommendation : recommendations){
            recs.add(getKey((int)recommendation.getItemID()));
        }
        return recs;
    }

    /**
     * Getter.
     * @return total reviews.
     */
    public int getTotalReviews()
    {
        return this.totalReviews;
    }

     /**
     * Getter.
     * @return total unique products.
     */
    public int getTotalProducts(){
        return this.uniqueProduct.size();
    }


     /**
     * Getter.
     * @return unique users size.
     */
    public int getTotalUsers(){
        return this.uniqueUser.size();
    }

}

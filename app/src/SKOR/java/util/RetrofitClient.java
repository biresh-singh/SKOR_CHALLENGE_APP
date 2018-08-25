package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import constants.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by biresh.singh on 05-08-2018.
 */

public class RetrofitClient {


    //public static final String BASE_URL = "http://110.172.163.219:8099/api/";
    /*public static final String BASE_URL = "http://jradmin.justrelief.com/api/";*/
    public static final String BASE_URL = Constants.BASESTAGINFURL+"challenges/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}

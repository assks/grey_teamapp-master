package in.technitab.teamapp.util;

import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

public class NetworkError {
    public static String getNetworkErrorMessage(Throwable error) {
        if (error instanceof SocketTimeoutException) {
            return "Poor internet connection";
        } else if (error instanceof MalformedJsonException) {
            return "Oops! We hit an error. Try again later.";
        } else if (error instanceof IOException) {
            return "Oh! You are not connected to a wifi or cellular data network.";
        } else if (error instanceof HttpException) {
            return (((HttpException) error).response().message());
        } else {
            return "Something went wrong!";
        }
    }


    public static String unsuccessfulResponseMessage(Integer statusCode){
        if (statusCode.equals(404)){
            return "Api not found";
        }else if (statusCode.equals(500)){
            return "Server broken";
        }else {
            return "Something went wrong!";
        }
    }
}

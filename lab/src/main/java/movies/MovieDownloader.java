package movies;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 * 
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

    //Method to download movie data
	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API
		String urlString = "http://www.omdbapi.com/?s=" + movie + "&type=movie";
		
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		String movies[] = null;

		try { //Attempt to try and make a connection to omdbapi

			URL url = new URL(urlString);

			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

            //Receive the data, and prepare to read it line by line.
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
            //While there is still lines to read
			while ((line = reader.readLine()) != null) {
				buffer.append(line + "\n");
			}

			if (buffer.length() == 0) {
				return null;
			}
            // In the buffer, replace various things to make it valid json
			String results = buffer.toString();
			results = results.replace("{\"Search\":[","");
			results = results.replace("]}","");
			results = results.replace("},", "},\n");

			movies = results.split("\n");
		} 
		catch (IOException e) {
			return null;
		} 
		finally {
            //This is called at the very end, closes the url connection.
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
            //Called at the end; closes the reader. 
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (final IOException e) {
				}
			}
		}

		return movies;
	}


    //This gets initialized first, starts up a scanner to get user input. 
    //Doesn't do a null check, as when it fails, it the movie data method returns null. 
	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Enter a movie name to search for: ");
        //Trims for white space but doesn't remove spaces inside. Spaces seem to break it.
		String searchTerm = sc.nextLine().trim();
		String[] movies = downloadMovieData(searchTerm);
		for(String movie : movies) {
			System.out.println(movie);
		}
		
		sc.close();
	}
}

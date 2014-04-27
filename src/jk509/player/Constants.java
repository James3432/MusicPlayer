package jk509.player;

import org.joda.time.DateTime;

import jk509.player.core.StaticMethods;

public interface Constants {

	/* 
	 * Things we might want to tweak (for research)
	 */
	
	// Learning - most values 0 <= v <= 1
	public static final double LEARNING_RATE = 0.1;                    // Learning Rate
	public static final double DISCOUNT_FACTOR = 0.0;                  // Discount Factor TODO: non-zero?
	public static final boolean PROBABILITIES_INITIALLY_SPREAD = false; // If true, P matrix initialised with all values = 1/size. If false, initialised to Identity
	public static final int HISTORY_SIZE = 20;
	public static final boolean BACK_UPDATES = true;                   // whether to update in reverse direction too
	public static final double BACK_UPDATE_SCALAR = 0.25;               // multiple to apply to reward for update in opposite direction
	public static final double RANDOMNESS_MIN = 0.2;                   // overall min value for randomness, taken at time=RANDOMNESS_SHIFT_TIME. User settings can't go below this.
	public static final double RANDOMNESS_MAX = 0.8;                   // overall max value for randomness, taken at time=0. User settings can't exceed this.
	public static final int RANDOMNESS_SHIFT_TIME = 10;                // number of days (or ops - I havn't decided) over which 'randomness' changes from exploration -> exploitation
	public static final double RANDOMNESS_USER_CONTROL = 0.4;          // range over which user can adjust randomness
	
	// Rewards:
	public static final double REWARD_TRACK_FINISHED = 0.5;            // track played all way through
	public static final double REWARD_TRACK_SKIPPED_MAX = 0.5;         // track barely skipped: should equal track_finished unless good reason otherwise
	public static final double REWARD_TRACK_SKIPPED_MIN = -0.8;        // track skipped straight away
	public static final double REWARD_TRACK_CHOSEN_MAX = 0.8;          // track changed after full play
	public static final double REWARD_TRACK_CHOSEN_MIN = 0.0;          // track changed straight away
	public static final double REWARD_TRACK_PLAYLIST = 0.4;            // tracks share a playlist
	public static final double REWARD_TRACK_PLAYLIST_ADJ = 0.8;        // tracks adajcent in playlist
	
	// Clustering
	public static final int MAX_CLUSTERS = 4;        // DEFAULT: 10
	public static final int KMEANS_SEED = 10;        // seed value for k-means algorithm
	
	// Feature extraction
	public static final int SAMPLING_RATE = 16;      // kHz
	public static final int ENCODE_BITRATE = 128;    // kbps
	public static final int WINDOW_SIZE = 512;       // window size
	public static final double WINDOW_OVERLAP = 0.0; // window overlap
	public static final boolean NORMALISE_FEATURES = false; // whether to normalise features myself. NOT IMPLEMENTED
	public static final boolean NORMALISE_AUDIO = false;    // whether to normalise audio before processing. NOT TESTED
	public static final boolean MULTITHREADED = true;       // whether to attempt feature extraction with multiple-core exploitation
	public static final int PARALELLISM = 4;                // see above: default number of threads to try (decremented upon failure)
	public static final boolean PARALLELISM_USE_PROC_COUNT = true; // Whether to set parallelism to the number of cores available
	
	// Other
	public static final int UPLOAD_FREQUENCY = 3;    // Data will be uploaded to server (url below) every n days
	public static final boolean SMART_PLAY_DEFAULT = true;  // Whether smart play mode is on by default
	//public static final double HISTORY_WEIGHT_MIN = 0.1;
	//public static final double HISTORY_WEIGHT_MAX = 1.0;
	public static final double HISTORY_WEIGHT_STEP = 0.01;   // ie. 1/this is how long it takes to forget we played a track
	public static final int HISTORY_NONREPEAT = 5; // number of tracks back in history we should never repeat
	
	// ------------------------------------------------------------------
	
	/*
	 * Debug
	 */
	
	public static final boolean DEBUG_LOAD_FEATURES_FILE = false; // whether to load features from disk rather than scanning
	public static final boolean DEBUG_SAVE_FEATURES = false;      // whether to save features to disk separately
	public static final boolean DEBUG_SAVE_CLUSTERS = false;      // whether to save clusters to disk separately
	public static final String FEATURES_PATH = "\\Music Factory\\features.ser"; // location for the above
	public static final String CLUSTERS_PATH = "\\Music Factory\\clusters.ser"; // location for the above
	public static final boolean CUSTOM_FEATUREPROC_CODE = false;  // whether to use the tweaked code which avoid errors but produces different results in feature extraction
	public static final boolean DEBUG_DISPLAY_UPDATES = true;     // display learning updates as they are processed
	public static final boolean DEBUG_NEXTTRACKPATHS = true;      // display details of track chosen by next()
	public static final boolean DEBUG_SETTINGS = true;            // use the debug version of settings.ser
	public static final boolean DEBUG_SHOW_SETUP = false;         // show setup screen always, for testing
	public static final boolean DEBUG_IGNORE_SETUP = false;        // ignore result of setup screen, load prog anyway
	
	// ------------------------------------------------------------------
	
	/*
	 * Things which are probably fixed
	 */
	
	public static final int JAUDIO_FEATURE_COUNT = 138; // #features in default list generated by features.xml
	public static final int FEATURES = 39; // 11 + 13 + 10 + 5 (last 3 are compound feature vectors which get linearised into array)
	public static final String featureXMLLocation = "features.xml";
	public static final int TEMP_FILE_NAME_LENGTH = 10;
	public static final String UPLOAD_URL = "http://www.james.eu.org/upload.php";
	public static final String SETTINGS_PATH = StaticMethods.getHomeDir() + (DEBUG_SETTINGS ? "\\Music Factory\\library_debug.ser" : "\\Music Factory\\library.ser");
	public static final String PATH_TO_LAME = StaticMethods.getHomeDir() + "\\Music Factory\\lame.exe";
	public static final boolean USERNAME_AS_ID = false; // Whether to capture current username as part of data upload ID string (cf. ethics agreement)

	public static final DateTime STUDY_START_DATE = new DateTime(2014, 4, 1, 12, 0, 0, 0);
	
}

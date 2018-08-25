package activity.surveypolling;

/**
 * Created by Ferry on 21/2/2018.
 */

public class Constants {
	//Intent Param
	public static final String PARAM_USER_TYPE = "user_type";
	public static final String PARAM_MODE = "survey_polling_mode";
	public static final String PARAM_SURVEY_POLLING_DATA = "survey_polling_data";
	public static final String PARAM_QUESTIONS = "questions";

	//Param Value
	public static final int TYPE_USER = 0;
	public static final int TYPE_ADMIN = 1;

	public static final int MODE_SURVEY = 0;
	public static final int MODE_POLLING = 1;

	//Url
	public static String GET_CATEGORY_LIST_API = "survey/api/survey_category/?pagination=false";
	public static String ADD_CATEGORY_API = "survey/api/survey_category/";
	public static String GET_DEPARTMENT_LIST_API = "profiles/api/departments/?pagination=false";
	public static String CREATE_SURVEY_POLLING_API = "survey/api/survey/";
	public static String VERIFY_REQUEST_LIST_API = "survey/api/survey/";
	public static String GET_MY_SURVEY = "survey/api/survey/?type=0";


	public static String generateGetSurveyPollingDetailApi(String id) {
		return "survey/api/survey/" + id + "/";
	}
	public static String generatePostRejectSurveyPollingApi(int id) {
		return "survey/api/survey/" + id + "/reject/";
	}
	public static String generatePostApproveSurveyPollingApi(int id) {
		return "survey/api/survey/" + id + "/approve/";
	}
	public static String generateAnswerSurveyPollingDetailApi(int id) {
		return "survey/api/survey/" + id + "/answer/";
	}
	public static String GET_VERIFY_BADGE_COUNT = "survey/api/survey/verify_survey_count/";
	public static String generateStatisticAnswerSurveyPollingApi(int id) {
		return "survey/api/survey/" + id + "/statistic_answer/";
	}
	public static String generateStatisticAnswerSurveyPollingApi(String id) {
		return "survey/api/survey/" + id + "/statistic_answer/";
	}
	public static String POST_SURVEY_POLLING_COMMENT_API = "survey/api/survey_comment/";
	public static String POST_SURVEY_POLLING_LIKE_API = "survey/api/survey_like/";
	public static String generateEditSurveyPollingApi(int id) {
		return "survey/api/survey/" + id + "/";
	}
	public static String generateShareResultApi(int id) {
		return "survey/api/survey/" + id + "/share_result/";
	}
}

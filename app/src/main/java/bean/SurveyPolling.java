package bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import io.realm.annotations.PrimaryKey;

/**
 * Created by mac on 2/20/18.
 */

public class SurveyPolling implements Parcelable {
    @PrimaryKey
    public int id;
    public String name;
    public Date startDate = new Date();
    public Date endDate = new Date();
    public String description;
    public int type;
    public ArrayList<Integer> topics = new ArrayList<>();
    public String state;
    public String creator;
    public boolean isCompleted;
    public int answerCount;
    public Date createdDate;
    public int recipientCount;
    public int viewCount;
    public ArrayList<Question> questions = new ArrayList<>();
    public ArrayList<StatisticAnswer> statisticAnswers = new ArrayList<>();
    public ArrayList<SurveyPollingComment> comments = new ArrayList<>();
    public ArrayList<SurveyPollingLike> likes = new ArrayList<>();
    public ArrayList<SurveyPollingTopic> surveyPollingTopics = new ArrayList<>();
    public int startAgeRestriction = 20;
    public int endAgeRestriction = 50;
    public int statusRestriction = STATUS_ALL;
    public int genderRestriction = GENDER_ALL;
    public ArrayList<Integer> departmentRestriction = new ArrayList<>(Arrays.asList(DEPARTMENT_ALL));
    public ArrayList<String> departmentName = new ArrayList<>();
    public long rewards = 0;
    public long creatorPoints = 0;

    //Const
    private static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private static SimpleDateFormat mDateJSONFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static final int TYPE_SURVEY = 0;
    public static final int TYPE_POLLING = 1;

    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;
    public static final int GENDER_ALL = 2;

    public static final int STATUS_ALL = 0;
    public static final int STATUS_SINGLE = 1;
    public static final int STATUS_MARRIED = 2;
    public static final int STATUS_DIVORCED = 3;

    public static final int DEPARTMENT_ALL = 0;

    public static final String STATE_DRAFT = "Draft";
    public static final String STATE_PUBLISHED = "Publish";
    public static final String STATE_REJECTED = "Rejected";
    public static final String STATE_COMPLETED = "Completed";
    public static final String STATE_SAVE_DRAFT = "Saved";

    public static final String COMPLETED_LABEL = "Completed";
    public static final String APPROVED_LABEL = "Approved";
    public static final String REJECTED_LABEL = "Rejected";
    public static final String SHARED_LABEL = "Shared";
    public static final String DRAFT_LABEL = "Draft";


    public SurveyPolling() {
    }

    public SurveyPolling(Parcel in) {
        id = in.readInt();
        name = in.readString();
        long startDateValue = in.readLong();
        if (startDateValue > 0)
            startDate = new Date(startDateValue);

        long endDateValue = in.readLong();
        if (endDateValue > 0)
            endDate = new Date(endDateValue);

        description = in.readString();
        type = in.readInt();
        in.readList(topics, String.class.getClassLoader());
        state = in.readString();
        creator = in.readString();
        isCompleted = in.readByte() == 1;
        answerCount = in.readInt();

        long createdDateValue = in.readLong();
        if (createdDateValue > 0)
            createdDate = new Date(createdDateValue);

        recipientCount = in.readInt();
        viewCount = in.readInt();

        in.readList(questions, QuestionOption.class.getClassLoader());
        in.readList(statisticAnswers, StatisticAnswer.class.getClassLoader());
        in.readList(comments, SurveyPollingComment.class.getClassLoader());
        in.readList(likes, SurveyPollingLike.class.getClassLoader());
        in.readList(surveyPollingTopics, SurveyPollingTopic.class.getClassLoader());

        startAgeRestriction = in.readInt();
        endAgeRestriction = in.readInt();
        statusRestriction = in.readInt();
        genderRestriction = in.readInt();

        departmentRestriction.clear();
        in.readList(departmentRestriction, Integer.class.getClassLoader());
        if (departmentRestriction.size() == 0) {
            departmentRestriction.add(DEPARTMENT_ALL);
        }

        in.readList(departmentName, String.class.getClassLoader());

        rewards = in.readLong();
    }

    public SurveyPolling(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                id = jsonObject.getInt("id");
            }
            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }
            if (jsonObject.has("start")) {
                String startDateText = jsonObject.getString("start");
                try {
                    startDate = mDateFormat.parse(startDateText + " 00:00:00");
                }
                catch (Exception e) {
                    Log.e("SurveyPolling", "Error when parsing start date");
                }
            }
            if (jsonObject.has("end")) {
                String endDateText = jsonObject.getString("end");
                try {
                    endDate = mDateFormat.parse(endDateText + " 23:59:59");
                }
                catch (Exception e) {
                    Log.e("SurveyPolling", "Error when parsing end date");
                }
            }
            if (jsonObject.has("description")) {
                description = jsonObject.getString("description");
            }
            if (jsonObject.has("type")) {
                type = jsonObject.getInt("type");
            }
            if (jsonObject.has("topics")) {
                JSONArray topicArray = jsonObject.getJSONArray("topics");
                int size = topicArray.length();
                for (int i=0; i<size; i++) {
                    int topic = topicArray.getInt(i);
                    topics.add(topic);
                }
            }
            if (jsonObject.has("state")) {
                state = jsonObject.getString("state");
            }
            if (jsonObject.has("creator")) {
                creator = jsonObject.getString("creator");
            }
            if (jsonObject.has("is_completed")) {
                isCompleted = jsonObject.getBoolean("is_completed");
            }
            if (jsonObject.has("created")) {
                String createdDateText = jsonObject.getString("created");
                try {
                    createdDate = mDateTimeFormat.parse(createdDateText);
                }
                catch (Exception e) {
                    Log.e("SurveyPolling", "Error when parsing created date");
                }
            }
            if (jsonObject.has("user_answer_count")) {
                answerCount = jsonObject.getInt("user_answer_count");
            }
            if (jsonObject.has("recipients")) {
                recipientCount = jsonObject.getInt("recipients");
            }
            if (jsonObject.has("total_view")) {
                viewCount = jsonObject.getInt("total_view");
            }
            if (jsonObject.has("questions")) {
                JSONArray questionsArray = jsonObject.getJSONArray("questions");

                questions.clear();
                int size = questionsArray.length();
                for (int i=0; i<size; i++) {
                    JSONObject questionsObject = questionsArray.getJSONObject(i);
                    Question question = new Question(questionsObject);
                    questions.add(question);
                }
            }
            if (jsonObject.has("survey_comments")) {
                JSONArray jsonArray = jsonObject.getJSONArray("survey_comments");
                int length = jsonArray.length();
                comments.clear();
                for (int i = 0; i < length; i++) {
                    comments.add(new SurveyPollingComment(jsonArray.getJSONObject(i)));
                }
            }
            if (jsonObject.has("survey_likes")) {
                JSONArray jsonArray = jsonObject.getJSONArray("survey_likes");
                int length = jsonArray.length();
                likes.clear();
                for (int i = 0; i < length; i++) {
                    likes.add(new SurveyPollingLike(jsonArray.getJSONObject(i)));
                }
            }
            if (jsonObject.has("read_topics")) {
                JSONArray jsonArray = jsonObject.getJSONArray("read_topics");
                int length = jsonArray.length();
                surveyPollingTopics.clear();
                for (int i = 0; i < length; i++) {
                    surveyPollingTopics.add(new SurveyPollingTopic(jsonArray.getJSONObject(i)));
                }
            }
            if (jsonObject.has("read_restriction_by_age")) {
                JSONArray restrictionByAgeArray = jsonObject.getJSONArray("read_restriction_by_age");
                int size = restrictionByAgeArray.length();
                if (size >= 2) {
                    startAgeRestriction = restrictionByAgeArray.getInt(0);
                    endAgeRestriction = restrictionByAgeArray.getInt(1);
                }
            }
            if (jsonObject.has("restriction_by_user_department")) {
                JSONArray restrictionByDepartmentArray = jsonObject.getJSONArray("restriction_by_user_department");

                departmentRestriction.clear();
                int size = restrictionByDepartmentArray.length();
                for (int i=0; i<size; i++) {
                    int restrictionByDepartment = restrictionByDepartmentArray.getInt(i);
                    departmentRestriction.add(restrictionByDepartment);
                }
            }
            if (jsonObject.has("read_department")) {
                JSONArray departmentNameArray = jsonObject.getJSONArray("read_department");

                departmentName.clear();
                int size = departmentNameArray.length();
                for (int i=0; i<size; i++) {
                    String department = departmentNameArray.getString(i);
                    departmentName.add(department);
                }
            }
            if (jsonObject.has("restriction_by_user_status")) {
                statusRestriction = jsonObject.getInt("restriction_by_user_status");
            }
            if (jsonObject.has("restriction_by_user_gender")) {
                genderRestriction = jsonObject.getInt("restriction_by_user_gender");
            }
            if (jsonObject.has("points")) {
                rewards = jsonObject.getInt("points");
            }
            if (jsonObject.has("points_creator")) {
                creatorPoints = jsonObject.getInt("points_creator");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean hasFinished() {
	    Date now = new Date();
	    return now.after(endDate);
    }

    public static JSONObject toJSONObject(SurveyPolling data) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", data.name);
            //jsonObject.put("description", data.description);
            jsonObject.put("start", mDateJSONFormat.format(data.startDate));
            jsonObject.put("end", mDateJSONFormat.format(data.endDate));
            jsonObject.put("type", data.type);

            JSONArray ageArray = new JSONArray();
            ageArray.put(data.startAgeRestriction);
            ageArray.put(data.endAgeRestriction);
            jsonObject.put("restriction_by_user_age", ageArray);

            JSONArray departmentArray = new JSONArray();
            for (Integer departmentId : data.departmentRestriction) {
                departmentArray.put(departmentId);
            }
            jsonObject.put("restriction_by_user_department", departmentArray);

            jsonObject.put("restriction_by_user_status", data.statusRestriction);
            jsonObject.put("restriction_by_user_gender", data.genderRestriction);

            JSONArray topicArray = new JSONArray();
            for (Integer categoryId : data.topics) {
                topicArray.put(categoryId);
            }
            jsonObject.put("topics", topicArray);

            JSONArray jsonArray = new JSONArray();
            for (Question question : data.questions) {
                jsonArray.put(Question.toJSONObject(question));
            }
            jsonObject.put("questions", jsonArray);
            jsonObject.put("state",data.state);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static final Parcelable.Creator<SurveyPolling> CREATOR = new Parcelable.Creator<SurveyPolling>() {
        public SurveyPolling createFromParcel(Parcel in) {
            return new SurveyPolling(in);
        }

        public SurveyPolling[] newArray(int size) {
            return new SurveyPolling[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(id);
        out.writeString(name);
        if (startDate != null)
            out.writeLong(startDate.getTime());
        else
            out.writeLong(-1);

        if (endDate != null)
            out.writeLong(endDate.getTime());
        else
            out.writeLong(-1);

        out.writeString(description);
        out.writeInt(type);
        out.writeList(topics);
        out.writeString(state);
        out.writeString(creator);
        out.writeByte((byte)(isCompleted ? 1 : 0));
        out.writeInt(answerCount);

        if (createdDate != null)
            out.writeLong(createdDate.getTime());
        else
            out.writeLong(-1);

        out.writeInt(recipientCount);
        out.writeInt(viewCount);
        out.writeList(questions);
        out.writeList(statisticAnswers);
        out.writeList(comments);
        out.writeList(likes);
        out.writeList(surveyPollingTopics);

        out.writeInt(startAgeRestriction);
        out.writeInt(endAgeRestriction);
        out.writeInt(statusRestriction);
        out.writeInt(genderRestriction);

        out.writeList(departmentRestriction);
        out.writeList(departmentName);
        out.writeLong(rewards);
    }
}

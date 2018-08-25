package bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ferry on 14/2/2018.
 */

public class Question implements Parcelable {
	public int id;
	public String title = "";
	public int type;
	public String answer = "";
	public int boolAnswer = -1;
	public List<QuestionOption> options = new ArrayList<QuestionOption>();
	public String yourAnswer;
	public int selectedRadioIndex = -1;
	public List<Integer> selectedCheckboxIndex = new ArrayList<Integer>();

	//Const
	public static final int TYPE_TEXT = 0;
	public static final int TYPE_SELECT_ONE_OR_MORE = 1;
	public static final int TYPE_YES_NO = 2;
	public static final int TYPE_MULTIPLE_CHOICE = 3;
	public static final int TYPE_MULTIPLE_CHOICE_WITH_OTHER = 4;

	public Question() {
	}

	public Question(Parcel in) {
		id = in.readInt();
		type = in.readInt();
		title = in.readString();
		answer = in.readString();
		boolAnswer = in.readInt();
		in.readList(options, QuestionOption.class.getClassLoader());
		yourAnswer = in.readString();
		selectedRadioIndex = in.readInt();
		in.readList(selectedCheckboxIndex, Integer.class.getClassLoader());
	}

	public Question(JSONObject jsonObject) {
		try {
			if (jsonObject.has("id")) {
				id = jsonObject.getInt("id");
			}
			if (jsonObject.has("title")) {
				title = jsonObject.getString("title");
			}
			if (jsonObject.has("type")) {
				type = jsonObject.getInt("type");
			}
			if (jsonObject.has("options")) {
				JSONArray optionArray = jsonObject.getJSONArray("options");
				int size = optionArray.length();
				for (int i=0; i<size; i++) {
					JSONObject optionObject = optionArray.getJSONObject(i);
					QuestionOption option = new QuestionOption(optionObject);
					options.add(option);
				}
			}
			if (jsonObject.has("your_answer")) {
				yourAnswer = jsonObject.getString("your_answer");
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static JSONObject toJSONObject(Question question) {
		JSONObject jsonObject = new JSONObject();
		try {
			if (question.id > 0)
				jsonObject.put("id", question.id);
			else
				jsonObject.put("id", JSONObject.NULL);


			jsonObject.put("title", question.title);
			jsonObject.put("type", question.type);

			JSONArray jsonArray = new JSONArray();
			if (question.type == TYPE_SELECT_ONE_OR_MORE || question.type == TYPE_MULTIPLE_CHOICE ||
					question.type == TYPE_MULTIPLE_CHOICE_WITH_OTHER) {

				for (QuestionOption option : question.options) {
					jsonArray.put(QuestionOption.toJSONObject(option));
				}
			}
			jsonObject.put("options", jsonArray);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
		public Question createFromParcel(Parcel in) {
			return new Question(in);
		}

		public Question[] newArray(int size) {
			return new Question[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int i) {
		out.writeInt(id);
		out.writeInt(type);
		out.writeString(title);
		out.writeString(answer);
		out.writeInt(boolAnswer);
		out.writeList(options);
		out.writeString(yourAnswer);
		out.writeInt(selectedRadioIndex);
		out.writeList(selectedCheckboxIndex);
	}
}

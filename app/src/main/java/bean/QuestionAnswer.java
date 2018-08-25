package bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ferry on 14/2/2018.
 */

public class QuestionAnswer implements Parcelable {
	public int id;
	public String answer;
	public int boolAnswer = -1;
	public int percentage;

	public QuestionAnswer(int id, String answer) {
		this.id = id;
		this.answer = answer;
	}

	public QuestionAnswer(int id, String answer, int boolAnswer) {
		this.id = id;
		this.answer = answer;
		this.boolAnswer = boolAnswer;
	}

	public QuestionAnswer(Parcel in) {
		id = in.readInt();
		answer = in.readString();
		boolAnswer = in.readInt();
		percentage = in.readInt();
	}

	public QuestionAnswer(JSONObject jsonObject) {
		try {
			if (jsonObject.has("id")) {
				id = jsonObject.getInt("id");
			}
			if (jsonObject.has("answer")) {
				answer = jsonObject.getString("answer");
			}
			if (jsonObject.has("percentage")) {
				percentage = jsonObject.getInt("percentage");
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static JSONObject toJSONObject(QuestionAnswer answer) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("id", answer.id);
			if (answer.boolAnswer == -1)
				jsonObject.put("answer", answer.answer);
			else
				jsonObject.put("answer", answer.boolAnswer);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public static final Creator<QuestionAnswer> CREATOR = new Creator<QuestionAnswer>() {
		public QuestionAnswer createFromParcel(Parcel in) {
			return new QuestionAnswer(in);
		}

		public QuestionAnswer[] newArray(int size) {
			return new QuestionAnswer[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int i) {
		out.writeInt(id);
		out.writeString(answer);
		out.writeInt(boolAnswer);
		out.writeInt(percentage);
	}
}

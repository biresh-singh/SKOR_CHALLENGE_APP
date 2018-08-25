package bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Ferry on 14/2/2018.
 */

public class StatisticAnswer implements Parcelable {
	public int questionId;
	public String question;
	public ArrayList<QuestionStatistic> answers = new ArrayList<>();

	public StatisticAnswer(Parcel in) {
		questionId = in.readInt();
		question = in.readString();
		in.readList(answers, QuestionStatistic.class.getClassLoader());
	}

	public StatisticAnswer(JSONObject jsonObject) {
		try {
			if (jsonObject.has("question_id")) {
				questionId = jsonObject.getInt("question_id");
			}
			if (jsonObject.has("question")) {
				question = jsonObject.getString("question");
			}
			if (jsonObject.has("answers_stat")) {
				JSONObject answerObject = jsonObject.getJSONObject("answers_stat");
				Iterator<String> iterator = answerObject.keys();
				while (iterator.hasNext()) {
					String question = iterator.next();
					int percentage = answerObject.getInt(question);
					QuestionStatistic statisticAnswer = new QuestionStatistic(question, percentage);
					answers.add(statisticAnswer);
				}
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static final Creator<StatisticAnswer> CREATOR = new Creator<StatisticAnswer>() {
		public StatisticAnswer createFromParcel(Parcel in) {
			return new StatisticAnswer(in);
		}

		public StatisticAnswer[] newArray(int size) {
			return new StatisticAnswer[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int i) {
		out.writeInt(questionId);
		out.writeString(question);
		out.writeList(answers);
	}
}

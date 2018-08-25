package adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.root.skor.R;

import java.nio.charset.Charset;
import java.util.ArrayList;


public class MultipleAddressAdapter extends BaseAdapter{
	
	Context mContext;
	ArrayList<String> addressArray=new ArrayList<String>();
	LayoutInflater layoutInflater;
	public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	 public MultipleAddressAdapter(Context context,ArrayList<String> _addressArray) {
		// TODO Auto-generated constructor stub
		 this.mContext=context;
		 this.addressArray=_addressArray;
		 layoutInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return addressArray.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		convertView=layoutInflater.inflate(R.layout.multiple_address_adapter, null);
		TextView addressTextView=(TextView)convertView.findViewById(R.id.address);
		String junkFreeString=addressArray.get(position);
		byte ptext[] = junkFreeString.getBytes(ISO_8859_1); 
		String value = new String(ptext, UTF_8);
		//String newString = junkFreeString.replaceAll("[^a-zA-Z]"," ");
		addressTextView.setText(value);
		addressTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
		return convertView;
	}

}

package mg.blueline.gulfsat.argusfinder.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mg.blueline.gulfsat.argusfinder.R;
import mg.blueline.gulfsat.argusfinder.myObjectModel.User;


/**
 * Created by hamidullah on 10/3/15.
 */
public class UserAdapter extends ArrayAdapter<User> {
    Context context;
    List<User> listUser;
    public UserAdapter(Context context, List<User> listUser) {

        super(context, android.R.id.content,listUser);
        Log.i("yasser", "kok");
        this.context=context;
        this.listUser=listUser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//       super.getView(position, convertView, parent);


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_users, null);

        ImageView i= (ImageView) view.findViewById(R.id.avatar);
        User user=listUser.get(position);
        TextView tv = (TextView) view.findViewById(R.id.name);
        tv.setText(user.getName());
       Log.i("yasser", "get view ici ");
        TextView tv1=(TextView) view.findViewById(R.id.location);
        tv1.setText("Près de " + user.getLastLocation());
        TextView d=(TextView) view.findViewById(R.id.time);
        d.setText("à " + user.getHd());



        return view;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(User item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}

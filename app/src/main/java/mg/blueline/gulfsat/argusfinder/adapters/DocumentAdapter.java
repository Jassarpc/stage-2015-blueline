package mg.blueline.gulfsat.argusfinder.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import mg.blueline.gulfsat.argusfinder.R;
import mg.blueline.gulfsat.argusfinder.myObjectModel.Document;


/**
 * Created by hamidullah on 9/25/15.
 */
public class DocumentAdapter extends ArrayAdapter<Document> {

     Context context;
     List<Document> listDoc;
    public DocumentAdapter(Context context,List<Document> listDoc) {
        super(context, android.R.id.content,listDoc);
        this.context=context;
        this.listDoc=listDoc;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_document, null);


        Document doc=listDoc.get(position);
        TextView tv = (TextView) view.findViewById(R.id.nom);
        tv.setText(doc.getName());
        Log.i("yasser", "getting done..." + doc.getName());
        TextView tv1=(TextView) view.findViewById(R.id.creator);
        tv1.setText("Crée par "+doc.getCreator());
        TextView d=(TextView) view.findViewById(R.id.date);
        d.setText("Le "+doc.getDate());
        Button icon= (Button) view.findViewById(R.id.icon);
        icon.setText(doc.getExtension());


        return view;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Document getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(Document item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}

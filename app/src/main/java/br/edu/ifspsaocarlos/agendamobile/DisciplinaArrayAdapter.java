package br.edu.ifspsaocarlos.agendamobile;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.edu.ifspsaocarlos.agendamobile.model.Contato;

public class DisciplinaArrayAdapter extends ArrayAdapter<Contato> {
	private LayoutInflater inflater;

	public DisciplinaArrayAdapter(Activity activity, List<Contato> objects) {
		super(activity, R.layout.contato_celula, objects);
		this.inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.contato_celula, null);
			holder = new ViewHolder();
			holder.nome = (TextView) convertView.findViewById(R.id.nome);
			holder.fone = (TextView) convertView.findViewById(R.id.fone);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Contato c = getItem(position);
		holder.nome.setText(c.getNome());
		holder.fone.setText(c.getFone());
		return convertView;
	}

	static class ViewHolder {
		public TextView nome;
		public TextView fone;
	}
}
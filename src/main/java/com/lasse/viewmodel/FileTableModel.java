package com.lasse.viewmodel;


import com.roha.srvcls.model.*;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.*;

/**
 * Container für Datenbank-Metadaten
 * @author Lasse Schöttner
 */
public class FileTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> feldnamen = new ArrayList<String>();
	private ArrayList<String> label = new ArrayList<String>();
	private ArrayList<Integer> dispSizes = new ArrayList<Integer>();
	private ArrayList<Integer> types = new ArrayList<Integer>();
	private ArrayList<String> schemas = new ArrayList<String>();
	private ArrayList<String> tables = new ArrayList<String>();
	private ArrayList<Integer> keynrs = new ArrayList<Integer>();
	private String typUeberschrift = "F";

	// store the column heading strings for our table...

	/**
	 * Override. Return string to shown in given column's heading area.
	 */
	public String getColumnName(int col) {
		if (typUeberschrift == null){
			typUeberschrift = "F";
		}
		if (typUeberschrift.equals("T")){
			if (col < label.size()){
				return label.get(col);
			}
		} else if (typUeberschrift.equals("B")){
			if (col < label.size() && col < feldnamen.size()){
				return feldnamen.get(col) + " " + label.get(col);
			}
		} else {
			if (col < feldnamen.size()){
				return feldnamen.get(col);
			}
		}
		return "???";
	}

	public String getFeldName(int col) {
		if (col < label.size() && col < feldnamen.size()){
			return feldnamen.get(col);
		}
		return "";
	}

	
	public String getColumnLabel(int col) {
		if (col < label.size()){
			return label.get(col);
		}
		return "???";
	}


	/**
	 * Override. Return total number of columns of data.
	 */
	public int getColumnCount() {
		return feldnamen.size();
	}



	public ArrayList<String> getFeldnamen() {
		return feldnamen;
	}

	public void setFeldnamen(ArrayList<String> feldnamen) {
		this.feldnamen = feldnamen;
	}

	
	
	public ArrayList<String> getLabel() {
		return label;
	}


	public void setLabel(ArrayList<String> label) {
		this.label = label;
	}


	public void setDispSizes(ArrayList<Integer> dispSizes) {
		this.dispSizes = dispSizes;
	}

	public void setTypes(ArrayList<Integer> types) {
		this.types = types;
	}

	

	public void setSchemas(ArrayList<String> schemas) {
		this.schemas = schemas;
	}


	public void setTables(ArrayList<String> tables) {
		this.tables = tables;
	}

	public void setKeynrs(ArrayList<Integer> keynrs) {
		this.keynrs = keynrs;
	}

	
	
	public ArrayList<Integer> getKeynrs() {
		return keynrs;
	}

	public void sortByColumn(int col) {

	}


	public String getTypUeberschrift() {
		return typUeberschrift;
	}


	public void setTypUeberschrift(String typUeberschrift) {
		this.typUeberschrift = typUeberschrift;
	}
	

	public TableView<Satz> initTableView(TableView<Satz> table){
		table.getColumns().clear();
		for (int i = 0; i < table.getColumns().size(); i++){
			table.getColumns().remove(i);
		}
		ArrayList<TableColumn<Satz,String>> liste = new ArrayList<TableColumn<Satz,String>>();
		for (int i = 0; i < feldnamen.size(); i++){
			TableColumn<Satz,String> col = new TableColumn<Satz,String>(feldnamen.get(i) + " " + label.get(i));
			double w = 15*dispSizes.get(i);
			if (w > 500) w = 500;
			//col.setPrefWidth(w);
			col.setUserData(feldnamen.get(i) + "!" + types.get(i));
			col.setCellValueFactory(new Callback<CellDataFeatures<Satz, String>, ObservableValue<String>>() {
				public ObservableValue<String> call(CellDataFeatures<Satz, String> p) {
					// p.getValue() returns the Person instance for a particular TableView row
					SqlWert sw = null;
					int type = java.sql.Types.CHAR;
					Object o = p.getTableColumn().getUserData();
					if (o != null && o instanceof String){
						String os = (String)o;
						String werte[] = os.split("!");
						String name = werte[0];
						type = new Integer(werte[1]);
						if (type == java.sql.Types.DECIMAL){
							sw = new SqlWert(p.getValue().getWertB(name), type);
						} else {
							sw = new SqlWert(p.getValue().getWertS(name), type);
						}
					} else {
						sw = new SqlWert("", type);
					}
					return sw;
				}
			});
			liste.add(col);
		}
		
		// Store the current sort policy
		Callback<TableView<Satz>, Boolean> currentSortPolicy = table.getSortPolicy();
		// Disble the sorting
		table.setSortPolicy(null);
		// Make all changes that might need or trigger sorting
		table.getColumns().addAll(liste);
		// Restore the sort policy that will sort the data once immediately
		table.setSortPolicy(currentSortPolicy);
		
		return table;
	}

} // end class OptionsTableModel

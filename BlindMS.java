/* Pete Parkinson
 * BlindMS.java
 * 8-24-18
 * 
 * My second iteration of MineSweeper.
 * trying to stay as close to the original as possible
 * more minimization is possible, but left as is for readability
 * 
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class BlindMS implements ActionListener, MouseListener{	
	
	public static void main(String[] args) {
		new BlindMS();
	}
	
	/*declarations
	 * opening difficulty set to beginner.
	 */
	static int i = 9;
	static int j = 9;
	static int mineCount = 10;
	static final int MINE = 9;
	
	int[][] minesAround = new int[i][j];
	
	JMenuBar bar 		 = new JMenuBar();
	JMenu settings 		 = new JMenu("Settings");
	JMenuItem difficulty = new JMenuItem("Difficulty");
	JFrame frame 		 = new JFrame("MineSweeper");
	JPanel topPanel 	 = new JPanel();
	
	JButton[][] cell = new JButton[i][j];
	JButton reset 	 = new JButton("Reset");
	JButton reveal 	 = new JButton("Reveal");
	
	Container mineField = new Container();
	
	//create GUI
	public BlindMS(){		
		
		reset.addActionListener(this);
		reveal.addActionListener(this);
		difficulty.addActionListener(this);
		
		cell = new JButton[i][j];
		
		for (int x = 0; x < i; x++) {
			for (int y = 0; y < j; y++) {
				cell[x][y] = new JButton();
				cell[x][y].addActionListener(this);
				cell[x][y].addMouseListener(this);
				mineField.add(cell[x][y]);
			}
		}

		bar.add(settings);
		settings.add(difficulty);

		topPanel.add(reset);
		topPanel.add(reveal);
		
		mineField.setLayout(new GridLayout(i, j));
		
		frame.setLayout(new BorderLayout());
		frame.setSize(j * 43, i *43);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(mineField, BorderLayout.CENTER);
		frame.setJMenuBar(bar);
		
		layMines();
	}

	
	private void layMines() {
		
		//mines per difficulty level
		if(j == 9) { mineCount = 10;}
		if(j == 16){ mineCount = 40;}
		if(j == 30){ mineCount = 99;}
		
		/*this creates a list of possible addresses for mines with cell 
		 * coordinates x coded into the hundreds place, y in the ones place.
		 */
		ArrayList<Integer> mineCandidates = new ArrayList<Integer>();
		minesAround = new int[i][j];
		for (int x = 0; x < i; x++) {
			for (int y = 0; y < j; y++) {
				mineCandidates.add(x * 100 + y);
			}
		}
		
		//create respective number of mines
		for (int a = 0; a < mineCount; a++) {
			int rdm = (int) (Math.random() * mineCandidates.size());
			minesAround[mineCandidates.get(rdm) / 100]
					[mineCandidates.get(rdm) % 100] = MINE;
			mineCandidates.remove(rdm);
		}
		
		//generates count of surrounding mines
		for (int x = 0; x < i; x++) {
			for (int y = 0; y < j; y++) {
				if(minesAround[x][y] != MINE){
					int mines = 0;
					for(int a = -1; a < 2; a++){
						for(int b = -1; b < 2; b++){
							try {
								if(a == 0 & b == 0){}
								else if(x > -1 
									 && y > -1
									 && x < i
									 && y < j
									 && minesAround[x + a][y + b] == MINE){
									
									mines++;
								}
							} catch (IndexOutOfBoundsException e) {}
						}
					}
					minesAround[x][y] = mines;
				}				
			}
		}
	}


	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == reset){
			reset();
		}
		if(event.getSource() == reveal){
			lose();
		}
		if(event.getSource() == difficulty){
			Object[] option = {"Beginner (9x9)", "Intermediate (16x16)", "Advanced(16x30)"};
			int result = JOptionPane.showOptionDialog(null, null, "Select Difficulty",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, option, null);
			if(result == JOptionPane.YES_OPTION){
				i = 9;
				j = 9;
			}
			if(result == JOptionPane.NO_OPTION){
				i = 16;
				j = 16;
			}
			if(result == JOptionPane.CANCEL_OPTION){
				i = 16;
				j = 30;
			}
			frame.dispose();
			new BlindMS();
		}
		else{
			//standard operation when clicking a button
			for (int x = 0; x < i; x++) {
				for (int y = 0; y < j; y++) {
					if(event.getSource().equals(cell[x][y])){
						clear(x, y);
						if(minesAround[x][y] == 0 && cell[x][y].getText() != "F") {
							ArrayList<Integer> clearMe = new ArrayList<Integer>();
							clearMe.add(x * 100 + y);
							//starts a list of cells to be cleared
							clearZeros(clearMe);		
						}
					}
				}
			}
		}
	}
	
	//clears contiguous cells with zero mines around them
	private void clearZeros(ArrayList<Integer> clearMe) {
		if(clearMe.size() == 0){
			return;  
		}
		if(clearMe.size() > 0){
			int x = clearMe.get(0) / 100;
			int y = clearMe.get(0) % 100;
			clearMe.remove(0);
			for (int a = -1; a < 2; a++) {
				for (int b = -1; b < 2; b++) {
					try {
						if(a == 0 & b == 0){}
						else if(x > -1 
							 && y > -1
							 && x < i
							 && y < j
							 && cell[x + a][y + b].isEnabled()
							 && cell[x + a][y + b].getText() != "F") {
							clear(x + a, y + b);
							if(minesAround[x + a][y + b] == 0){
								clearMe.add((x + a) * 100 + (y + b));
							}
						}
					} catch (IndexOutOfBoundsException e) {}
				}
			}
		}
		//calls itself until list reaches 0 size
		clearZeros(clearMe);		
	}
	
	private void checkWin() {
		boolean win = true;
		for (int x = 0; x < i; x++) {
			for (int y = 0; y < j; y++) {
				if(cell[x][y].isEnabled() && cell[x][y].getText() != "F" 
						&& minesAround[x][y] != MINE){
					win = false;
				}
				if(cell[x][y].getText() == "F" && minesAround[x][y] != MINE){
					win = false;
				}
				if(cell[x][y].getText() == "X"){
					win = false;
				}
			}
		}
		if(win == true){
			for (int x = 0; x < i; x++) {
				for (int y = 0; y < j; y++) {
					if(minesAround[x][y] == MINE){
						cell[x][y].setText("F");
						cell[x][y].setForeground(Color.black);
					}
				}
			}
			JOptionPane.showMessageDialog(null, "You Win!");
		}
	}


	private void clear(int x, int y) {
		if(minesAround[x][y] == MINE){
			lose();
		}
		if(cell[x][y].isEnabled() && cell[x][y].getText() != "X" && cell[x][y].getText() != "F"){
			cell[x][y].setEnabled(false);
			if(minesAround[x][y] != 0){
				cell[x][y].setText(minesAround[x][y] + "");
			}
		}
		checkWin();
	}


	private void lose() {
		for (int x = 0; x < i; x++) {
			for (int y = 0; y < j; y++) {
				if(minesAround[x][y] == 0){
					cell[x][y].setText("");
					cell[x][y].setEnabled(false);
				}
				if(minesAround[x][y] == MINE){
					cell[x][y].setText("X");
					cell[x][y].setForeground(Color.red);
				}
				if(minesAround[x][y] != 0 && minesAround[x][y] != MINE){
					cell[x][y].setText(minesAround[x][y] + "");
					cell[x][y].setEnabled(false);
				}
			}
		}
	}


	private void reset() {
		for (int x = 0; x < i; x++) {
			for (int y = 0; y < j; y++) {
				cell[x][y].setText("");
				cell[x][y].setEnabled(true);
				cell[x][y].setForeground(Color.black);
				
				layMines();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		JButton tmp = (JButton) event.getSource();
		if(event.getButton() == MouseEvent.BUTTON3 && tmp.isEnabled()){
			if(tmp.getText() == ""){
				tmp.setText("F");
			}
			else if(tmp.getText() == "F"){
				tmp.setText("");
			}
		}
		if(event.getButton() == MouseEvent.BUTTON3 && tmp.isEnabled() ==  false){
			
			for (int x = 0; x < i; x++) {
				for (int y = 0; y < j; y++) {
					if(tmp == cell[x][y] && minesAround[x][y] == cycleNeighbors(x, y, 0)){
						cycleNeighbors(x, y, 1);
					}
				}
			}
		}
	}
	
	
	private int cycleNeighbors(int x, int y, int ref) {
		/**
		 * ref 0 = cycle for flags
		 * ref 1 = clear neighbors
		 * not sure this is a good idea
		 * there are 3 functions that cycle through neighboring cells
		 * but each one is different, and combining them destroys
		 * readability
		 */
		int flags = 0;
		for(int a = -1; a < 2; a++){
			for(int b = -1; b < 2; b++){
				try {
					if(a == 0 & b == 0){
					}else if(x > -1 && y > -1 && x < i && y < j){
						if(cell[x + a][y + b].getText() != "F" && ref == 1){
							clear(x + a, y + b);
							if(minesAround[x + a][y + b] == 0){
								ArrayList<Integer> clearMe = new ArrayList<Integer>();
								clearMe.add((x + a) * 100 + (y + b));
								clearZeros(clearMe);
							}
						}
						if(cell[x + a][y + b].getText() == "F" && ref == 0){
							{flags++;}
						}
					}
				} catch (IndexOutOfBoundsException e) {}
			}
		}
		return flags;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		
	}

	@Override
	public void mouseExited(MouseEvent event) {
		
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		
	}

}

package Game_2048;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Game2048 extends JFrame implements KeyListener, ActionListener {
	Container ct = getContentPane();
	JPanel[][] box = new JPanel[4][4]; // 4*4 박스를 나타낼 패널
	JLabel[][] jl = new JLabel[4][4]; // 박스 안의 값(숫자 표시용)
	JLabel printscore = new JLabel();
	JLabel gameovertext = new JLabel();
	int[][] num = new int[4][4]; // 레이블에 대입할 실질적인 값
	int score = 0;
	final int scoreratio = 10; // 점수비율
	int countblock = 0;
	File_IO fi = new File_IO();
	JTextArea rank = new JTextArea();
	JTextArea ranknum = new JTextArea();
	JTextArea wja = new JTextArea();

	public Game2048()
	{
		initialize();
		initialize_ranking();
		randMake();
		refresh();
		setTitle("2048");
		setSize(1200,700);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void initialize()
	{
		ct.setLayout(new BorderLayout());
		
		//오른쪽 서브패널
		JPanel sub = new JPanel();
		sub.setLayout(new GridLayout(10,1));
		sub.setBackground(Color.lightGray);
		sub.setBackground(new Color(213,240,251));
		
		//Welcome 2048
		JLabel welcome = new JLabel("Welcome 2048!");
		welcome.setFont(new Font("나눔고딕", Font.BOLD, 45));
		welcome.setHorizontalAlignment(SwingConstants.CENTER);
		sub.add(welcome);
		
		//점수(글자, 라벨)
		JLabel lscore = new JLabel("          점수          ");
		lscore.setFont(new Font("나눔고딕", Font.BOLD, 35));
		lscore.setHorizontalAlignment(SwingConstants.CENTER);
		sub.add(lscore);

		//점수(숫자)
		printscore.setHorizontalAlignment(SwingConstants.CENTER);
		printscore.setFont(new Font("나눔고딕", Font.BOLD, 35));
		printscore.setText(Integer.toString(score));
		sub.add(printscore);
	
		//게임오버 aptlwl
		gameovertext.setText("");
		gameovertext.setHorizontalAlignment(SwingConstants.CENTER);
		gameovertext.setFont(new Font("나눔고딕", Font.BOLD, 45));
		gameovertext.setForeground(Color.red);
		sub.add(gameovertext); 
		
		//developer 메시지
		JLabel developer = new JLabel("Developed by 전윤호");
		developer.setFont(new Font("나눔고딕", Font.BOLD, 13));
		developer.setHorizontalAlignment(SwingConstants.CENTER);
		sub.add(developer);

		//powered 메시지
		JLabel powered = new JLabel("Powered by Java");
		powered.setFont(new Font("나눔고딕", Font.BOLD, 13));
		powered.setHorizontalAlignment(SwingConstants.CENTER);
		sub.add(powered);
		
		//안내메시지
		JLabel notice = new JLabel("종료할땐 '게임 종료' 버튼을 클릭! 강제 종료 시 점수가 저장되지 않습니다!");
		notice.setFont(new Font("나눔고딕", Font.BOLD, 10));
		notice.setHorizontalAlignment(SwingConstants.CENTER);
		sub.add(notice);
	
		//재시작 버튼
		JButton restart = new JButton("재시작");
		restart.setFont(new Font("나눔고딕", Font.BOLD, 45));
		restart.setBackground(new Color(206,222,255));
		restart.addActionListener(this);
		restart.addKeyListener(this);
		sub.add(restart);
		
		//랭킹리셋 버튼
		JButton rankreset = new JButton("랭킹 초기화");
		rankreset.setFont(new Font("나눔고딕", Font.BOLD, 45));
		rankreset.setBackground(new Color(222,219,255));
		rankreset.addActionListener(this);
		rankreset.addKeyListener(this);
		sub.add(rankreset);
		
		//게임 종료 버튼
		JButton exitbutton = new JButton("게임 종료");
		exitbutton.setFont(new Font("나눔고딕", Font.BOLD, 45));
		exitbutton.setBackground(new Color(158,184,250));
		exitbutton.addActionListener(this);
		sub.add(exitbutton);
		

		//메인 프레임 (중앙 4*4 그리드 판)
		JPanel fraim = new JPanel();
		fraim.setLayout(new GridLayout(4,4,3,3));
		for(int i = 0; i<4; i++)
		{
			for(int j = 0; j<4; j++)
			{
				num[i][j] = 0;
				box[i][j] = new JPanel();
				box[i][j].setLayout(new GridLayout(1,1));
				box[i][j].setBackground(Color.white);		
				jl[i][j] = new JLabel();
				jl[i][j].setFont(new Font("맑은 고딕", Font.BOLD, 45));
				jl[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				jl[i][j].setVerticalAlignment(SwingConstants.CENTER);
				box[i][j].add(jl[i][j]);
				fraim.add(box[i][j]);
			}
		}
		ct.add(sub,BorderLayout.EAST);
		ct.add(fraim,BorderLayout.CENTER);
	}
	
	public int[] sort(int[] arr)
	{
		for(int i = arr.length - 1; i>0; i-- )
			for(int j = 0; j<i; j++)
				if(arr[j] < arr[j + 1])
				{
					int temp = arr[j];
					arr[j] = arr[j+1];
					arr[j+1] = temp;
				}
		return arr;
	}

	public void initialize_ranking()
	{
		//왼쪽 서브 패널
		JPanel west = new JPanel();
		west.setLayout(new BorderLayout());
		
		//상단 랭크 텍스트
		JPanel textPanel = new JPanel();
		JLabel westText = new JLabel("랭킹");
		textPanel.add(westText);
		westText.setFont(new Font("나눔고딕", Font.BOLD, 30));
		textPanel.setBackground(new Color(145,181,255));
		west.add(textPanel,BorderLayout.NORTH);
		
		
		//순위, 점수 패널
		JPanel rankpanel = new JPanel();
		rankpanel.setLayout(new BorderLayout());
		ranknum.setEditable(false);
		ranknum.addKeyListener(this);
		rank.addKeyListener(this);
		rank.setEditable(false);
		rankpanel.add(ranknum,BorderLayout.WEST);
		rankpanel.add(rank,BorderLayout.CENTER);
		ranknum.setBackground(new Color(198,217,255));
		rank.setBackground(new Color(219,231,255));
		ranknum.setFont(new Font("나눔고딕", Font.BOLD, 20));
		rank.setFont(new Font("나눔고딕", Font.BOLD, 20));
		
		//점수 "점"
		rankpanel.add(wja,BorderLayout.EAST);
		wja.setEditable(false);
		wja.setBackground(new Color(219,231,255));
		wja.setFont(new Font("나눔고딕", Font.BOLD, 20));

		west.add(rankpanel,BorderLayout.CENTER);
		
		importRank();
		
		ct.add(west,BorderLayout.WEST);
	}
	
	public void importRank()
	{
		ranknum.setText("");
		rank.setText("");
		wja.setText("");
		
		String ranklist = fi.read_word();
		if(ranklist.equals(""))
		{
			ranknum.append("1위 ");
			rank.append(" 0 ");
			wja.append(" 점 ");
			return;
		}
		
		String[] str = ranklist.split("\n");
		int[] temp = new int[str.length];
		
		for(int i = 0; i<str.length; i++)
			temp[i] = Integer.parseInt(str[i]);
		
		temp = sort(temp);
		
		for(int i = 0; i<str.length; i++)
		{
			ranknum.append(" "+String.valueOf(i+1)+"위 \n");
			rank.append(" "+temp[i]+"\n");
			wja.append(" 점 \n");
		}
	}
	
	public void saveRank()
	{
		if(score != 0)	
			fi.saveFile(score+"\n");
	}
	
	public void keyReleased(KeyEvent e) {} // 오버라이딩
	public void keyTyped(KeyEvent e) {} // 오버라이딩
	public void keyPressed(KeyEvent e) 
	{
		int key = e.getKeyCode();
		activate(key); //블록 합치기 한쪽으로 몰기
		refresh(); //새로고침
	}

	public void randMake()
	{
		Random rd = new Random();
		int count = 0;
		int max = rd.nextInt(2)+1;
		if(countblock >= 12)
			max = 1;
	
		while(true)
		{	
			//디버깅중======
			if(isGameover() == true)
			{
				gameovertext.setText("Game Over!");
				break;
			}
			//============
			
			if(count == max)
				break;
			
			int x = rd.nextInt(4);
			int y = rd.nextInt(4);
			
			if(num[x][y] != 0)
				continue;
			
			int temp = rd.nextInt(3); // 0, 1, 2 랜덤 난수 생성
			if(temp == 0) temp = 4;
			else if(temp == 1) temp = 2; // 2가 나올 확률은 2/3, 4가 나올 확률은 1/3
			
			num[x][y] = temp;
			jl[x][y].setText(Integer.toString(num[x][y]));
			count++;
		}
	}
	
	public boolean emptyblock()
	{
		countblock = 0;
		boolean empty = false;
		
		for(int i = 0; i<4; i++)
		{
			for(int j = 0; j<4; j++)
			{
				if(num[i][j] == 0)
					empty = true;
				else
					countblock++;
			}
		}
		
		return empty;
	}
	
	public boolean isGameover()
	{
		boolean check = false;
		int count = 0;
		
		for(int i = 0; i<3; i++)
			for(int j = 0; j<4; j++)
				if(num[i][j] != num[i+1][j] && num[i][j] != 0)
					count++;
		
		for(int i = 0; i<3; i++)
			for(int j = 0; j<4; j++)
				if(num[j][i] != num[j][i+1] && num[j][i] != 0 && num[j][i+1] != 0)
					count++;

		if(count == 24)
			check = true;
				
		return check;
	}
	
	public void activate(int key) // 블록 합치기, 한쪽으로 몰기
	{
//		 37 왼쪽
//		 38 위쪽
//		 39 오른쪽
//		 40 아래쪽
//		
//      좌표		
//		
//		0,0     1,0     2,0     3,0
//
//		0,1     1,1     2,1     3,1
//
//		0,2     1,2     2,2     3,2
//
//		0,3     1,3     1,3     3,3
		
		boolean br = false;
		
		if(key == 37) // 왼쪽
		{
			for(int i = 0; i<4; i++)
			{
				for(int j = 1; j<=3; j++)
				{
					if(num[i][j-1] == 0 && num[i][j]>0)
					{
						num[i][j-1] = num[i][j];
						num[i][j]=0;
						i--;
						br = true;
						break;
					}
				}
				if(br == true)
				{
					br = false;
					continue;
				}
				for(int j = 0; j<3; j++)
				{
					if(num[i][j] == num[i][j+1])
					{
						score += (num[i][j]*scoreratio);
						num[i][j] *= 2;
						num[i][j+1] = 0;
					}
				}
				// ************************** 디버깅 부분, 에러 있을 확률 있음
				for(int j = 1; j<=3; j++)
				{
					if(num[i][j-1] == 0 && num[i][j]>0)
					{
						num[i][j-1] = num[i][j];
						num[i][j]=0;
					}
				}
				// **************************** 디버깅 부분
			}				
		}
		
		else if(key == 39) // 오른쪽
		{
			for(int i = 0; i<4; i++)
			{
				for(int j = 2; j>=0; j--)
				{
					if(num[i][j+1] == 0 && num[i][j]>0)
					{
						num[i][j+1] = num[i][j];
						num[i][j]=0;
						i--;
						br = true;
						break;
					}
				}
				if(br == true)
				{
					br = false;
					continue;
				}
				for(int j = 2; j>=0; j--)
				{
					if(num[i][j+1] == num[i][j])
					{
						score += (num[i][j]*scoreratio);
						num[i][j+1] *= 2;
						num[i][j] = 0;
					}
				}
				for(int j = 2; j>=0; j--)
				{
					if(num[i][j+1] == 0 && num[i][j]>0)
					{
						num[i][j+1] = num[i][j];
						num[i][j]=0;
					}
				}
			}
		}
		
		else if(key == 38) // 위쪽
		{
			for(int i = 0; i<4; i++)
			{
				for(int j = 1; j<=3; j++)
				{
					if(num[j-1][i] == 0 && num[j][i]>0)
					{
						num[j-1][i] = num[j][i];
						num[j][i]=0;
						i--;
						br = true;
						break;
					}
				}
				if(br == true)
				{
					br = false;
					continue;
				}
				for(int j = 0; j<3; j++)
				{
					if(num[j][i] == num[j+1][i])
					{
						score += (num[j][i]*scoreratio);
						num[j][i] *= 2;
						num[j+1][i] = 0;
					}
				}
				for(int j = 1; j<=3; j++)
				{
					if(num[j-1][i] == 0 && num[j][i]>0)
					{
						num[j-1][i] = num[j][i];
						num[j][i]=0;
					}
				}
			}				
		}
		
		else if(key == 40) // 아래쪽
		{
			for(int i = 0; i<4; i++)
			{
				for(int j = 2; j>=0; j--)
				{
					if(num[j+1][i] == 0 && num[j][i]>0)
					{
						num[j+1][i] = num[j][i];
						num[j][i]=0;
						i--;
						br = true;
						break;
					}
				}
				if(br == true)
				{
					br = false;
					continue;
				}
				for(int j = 2; j>=0; j--)
				{
					if(num[j+1][i] == num[j][i])
					{
						score += (num[j][i]*scoreratio);
						num[j+1][i] *= 2;
						num[j][i] = 0;
					}
				}
				for(int j = 2; j>=0; j--)
				{
					if(num[j+1][i] == 0 && num[j][i]>0)
					{
						num[j+1][i] = num[j][i];
						num[j][i]=0;
					}
				}
			}				
		}
		else // 방향키 외 키값이 들어올 경우 아무것도 안하고 메소드 종료
			return;
		
		if(emptyblock())
			randMake(); //숫자생성
	}
	
	public void actionPerformed(ActionEvent e)
	{
		saveRank();
		String str = e.getActionCommand();
		if(str.equals("게임 종료"))
			System.exit(0);
		else if(str.equals("재시작"))
			restart();
		else if(str.equals("랭킹 초기화"))
			rankreset();
	}
	
	public void rankreset()
	{
		fi.rankreset();
		importRank();
	}
	
	public void restart()
	{
		for(int i = 0; i<4; i++)
			for(int j = 0; j<4; j++)
				num[i][j] = 0;
		score = 0;
		gameovertext.setText("");
		countblock = 0;
		randMake();
		importRank();
		refresh();
	}
	
	public void refresh()
	{
		printscore.setText(Integer.toString(score));
		for(int i = 0; i<4; i++)
		{
			for(int j = 0; j<4; j++)
			{
				if(num[i][j] == 0)
				{
					jl[i][j].setText("");
					box[i][j].setBackground(Color.white);
				}
				else
				{
					jl[i][j].setText(Integer.toString(num[i][j]));
					
					if(num[i][j] <= 2)
						box[i][j].setBackground(new Color(243,243,243));
					else if(num[i][j] == 4)
						box[i][j].setBackground(new Color(255,228,185));
					else if(num[i][j] == 8)
						box[i][j].setBackground(new Color(255,208,130));
					else if(num[i][j] == 16)
						box[i][j].setBackground(new Color(255,172,49));
					else if(num[i][j] == 32)
						box[i][j].setBackground(new Color(255,168,0));
					else if(num[i][j] == 64)
						box[i][j].setBackground(new Color(255,66,66));
					else if(num[i][j] <= 256)
						box[i][j].setBackground(new Color(255,233,28));
					else if(num[i][j] <= 2048)
						box[i][j].setBackground(new Color(255,28,28));
					else
					{
						box[i][j].setBackground(Color.black);
						jl[i][j].setForeground(Color.white);
					}
				}
			}
		}
	}
}
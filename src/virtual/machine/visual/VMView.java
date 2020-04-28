package virtual.machine.visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import static lib.FOOLLib.MEMSIZE;

/**
 * GUI of the visual virtual machine
 * 
 * @author Paolo Baldini
 */
public class VMView {

	private static final Font FONT = new Font( Font.MONOSPACED, Font.PLAIN, 12 );

	private final VMCore vm;

	private int[] sourceMap;

	private final JFrame frame;
	private final JPanel mainPanel;
	private final JPanel eastPanel;
	private final JPanel buttonPanel;
	private final JList<String> asmList;
	private final JList<String> stackList, heapList;
	private final JButton nextStep;
	private final JButton backStep;
	private final JButton play;
	private final JPanel registerPanel;
	private final JSplitPane memPanel;
	private final JLabel tmLabel, raLabel, fpLabel, ipLabel, spLabel, hpLabel;
	private final JScrollPane asmScroll, stackScroll, heapScroll, outputScroll;
	private final JTextArea outputText;
	private final JTextArea infoBox;

	private final int codeLineCount;
	private String keyboardCommand = "";
	private int breakPointLine = -1;

	public VMView ( VMCore vm, int[] sourceMap, List<String> source ) {

		this.vm = vm;
		this.sourceMap = sourceMap;

		frame = new JFrame( "FOOL Virtual Machine" );
		mainPanel = new JPanel( );
		eastPanel = new JPanel( );

		eastPanel.setLayout( new BorderLayout( ) );

		buttonPanel = new JPanel( );
		buttonPanel.setLayout( new BorderLayout( ) );
		play = new JButton( "PLAY" );
		play.addActionListener( e -> playButtonHandler( ) );

		nextStep = new JButton( "NEXT STEP" );
		nextStep.addActionListener( e -> nextStepButtonHandler( ) );
		backStep = new JButton( "BACK STEP" );
		backStep.addActionListener( e -> backStepButtonHandler( ) );
		infoBox = new JTextArea( "info:\n\nDouble click on\na cell will set a\nbreakpoint!\n" );
		infoBox.setEditable( false );
		buttonPanel.add( play, BorderLayout.NORTH );
		buttonPanel.add( nextStep, BorderLayout.CENTER );
		buttonPanel.add( backStep, BorderLayout.SOUTH );
		eastPanel.add( buttonPanel, BorderLayout.NORTH );
		eastPanel.add( Box.createVerticalGlue( ), BorderLayout.CENTER );
		eastPanel.add( new JScrollPane( infoBox ), BorderLayout.SOUTH );

		registerPanel = new JPanel( );
		tmLabel = new JLabel( );
		tmLabel.setFont( FONT );
		raLabel = new JLabel( );
		raLabel.setFont( FONT );
		fpLabel = new JLabel( );
		fpLabel.setFont( FONT );
		ipLabel = new JLabel( );
		ipLabel.setFont( FONT );
		spLabel = new JLabel( );
		spLabel.setFont( FONT );
		hpLabel = new JLabel( );
		hpLabel.setFont( FONT );
		registerPanel.setLayout( new BoxLayout( registerPanel, BoxLayout.Y_AXIS ) );
		registerPanel.add( tmLabel );
		registerPanel.add( raLabel );
		registerPanel.add( fpLabel );
		registerPanel.add( ipLabel );
		registerPanel.add( spLabel );
		registerPanel.add( hpLabel );

		mainPanel.setLayout( new BorderLayout( ) );
		asmList = new JList<String>( );

		for ( int i = 0; i < source.size( ); i++ ) {
			source.set( i, String.format("%5d: %s", i, source.get( i ) ) );
		}
		asmList.setListData( new Vector<>( source ) );
		codeLineCount = source.size( );


		asmList.setFont( FONT );
		asmList.addMouseListener( new MouseAdapter( ) {
			public void mouseClicked( MouseEvent evt ) {
				JList<?> list = ( JList<?> ) evt.getSource( );
				if ( evt.getClickCount( ) == 2 ) { // Double-click detected
					int index = list.locationToIndex( evt.getPoint( ) );
					if ( breakPointLine == index ) breakPointLine = -1;
					else breakPointLine = index;
					SwingUtilities.updateComponentTreeUI( asmList );
				}
			}
			public void mouseReleased( MouseEvent e ) {
				asmList.setSelectedIndex( sourceMap[vm.getState( ).getIp( )] );
			}
		});
		for ( MouseMotionListener m : asmList.getMouseMotionListeners( ) ) {
			asmList.removeMouseMotionListener( m );
		}
		asmList.setCellRenderer( new DefaultListCellRenderer() {
			private static final long serialVersionUID = -4794316536883433342L;

			@Override
			public Component getListCellRendererComponent( JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
				JLabel label = ( JLabel ) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

				if ( index == breakPointLine )
					label.setBackground( Color.RED );
				return label;
			}
		});

		asmScroll = new JScrollPane( asmList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		mainPanel.add( asmScroll, BorderLayout.EAST );

		stackList = new JList<>( );
		heapList = new JList<>( );
		setMem( );
		stackList.setFont( new Font( Font.MONOSPACED, Font.BOLD, 16 ) );
		heapList.setFont( new Font( Font.MONOSPACED, Font.BOLD, 16 ) );
		stackScroll = new JScrollPane( stackList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		heapScroll = new JScrollPane( heapList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		memPanel = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
				stackScroll, heapScroll );
		mainPanel.add( memPanel, BorderLayout.CENTER );

		outputText = new JTextArea( );
		outputText.setRows( 7 );
		outputText.setEditable( false );
		outputScroll = new JScrollPane( outputText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );


		frame.getContentPane( ).setLayout( new BorderLayout( ) );
		frame.add( mainPanel, BorderLayout.CENTER );
		frame.add( eastPanel, BorderLayout.EAST );
		frame.add( registerPanel, BorderLayout.WEST );
		frame.add( outputScroll, BorderLayout.SOUTH );

		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		outputText.addKeyListener( new KeyListener( ) {
			@Override
			public void keyTyped( KeyEvent e ) {
				keyboardCommand += e.getKeyChar( );
				checkKeyboardCommand( );
			}
			@Override
			public void keyReleased( KeyEvent e ) { }
			@Override
			public void keyPressed( KeyEvent e ) { }
		} );

		update( );
		frame.setMinimumSize( new Dimension( 800, 500 ) );
		frame.pack( );

		stackScroll.getVerticalScrollBar( ).setValue( stackScroll.getVerticalScrollBar( ).getMaximum( ) );
		memPanel.setDividerLocation( 0.5 );

		Thread.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler( ) {
			@Override
			public void uncaughtException( Thread t, Throwable e ) {
				e.printStackTrace( );
				System.exit( 1 );
			}
		} );

		frame.setVisible( true );
	}

	private void checkKeyboardCommand( ) {
		if ( keyboardCommand.endsWith( " " ) )
			nextStepButtonHandler( );
		else if ( keyboardCommand.endsWith( "\n" ) )
			playButtonHandler( );

		keyboardCommand = "";
	}

	private void setMem( ) {
		stackList.setListData( new Vector<>( IntStream.range( 0, MEMSIZE )
				.mapToObj( x -> String.format( "%5d: %s", x, x <= vm.getState( ).getHp( )
						|| x >= vm.getState( ).getSp( ) ? vm.getState( ).getMemory( )[x] : "" ) )
				.collect( Collectors.toList( ) ) ) );
		heapList.setListData( new Vector<>( IntStream.range( 0, MEMSIZE )
				.mapToObj( x -> String.format( "%5d: %s", x, x <= vm.getState( ).getHp( )
						|| x >= vm.getState( ).getSp( ) ? vm.getState( ).getMemory( )[x] : "" ) )
				.collect( Collectors.toList( ) ) ) );
	}

	private void update( ) {
		raLabel.setText( "RA: " + vm.getState( ).getRa( ) );
		fpLabel.setText( "FP: " + vm.getState( ).getFp( ) );
		tmLabel.setText( "TM: " + vm.getState( ).getTm( ) );
		ipLabel.setText( "IP: " + vm.getState( ).getIp( ) );
		hpLabel.setText( "HP: " + vm.getState( ).getHp( ) );
		spLabel.setText( "SP: " + vm.getState( ).getSp( ) );

		asmList.clearSelection( );
		asmList.setSelectedIndex( sourceMap[vm.getState( ).getIp( )] );
		SwingUtilities.updateComponentTreeUI( asmList );

		final JScrollBar s = asmScroll.getVerticalScrollBar( );
		int dest = sourceMap[vm.getState( ).getIp( )] * s.getMaximum( ) / codeLineCount - s.getHeight( ) / 2;
		s.setValue( Math.max( dest, 0 ) );
		setMem( );

		if ( vm.getState( ).getResult( ).isPresent( ) )
			outputText.setText( vm.getState( ).getResult( ).get( ) + "\n" );
		else outputText.setText( "" );
	}

	private void playButtonHandler( ) {
		while ( ! vm.hasEnded( ) && sourceMap[vm.getState( ).getIp( )] != breakPointLine )
			vm.nextStep( );
		update( );
	}

	private void nextStepButtonHandler( ) {
		vm.nextStep( );
		update( );
	}

	private void backStepButtonHandler( ) {
		vm.backStep( );
		update( );
	}
}

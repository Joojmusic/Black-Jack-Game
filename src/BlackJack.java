import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class BlackJack {

    public class Card{
        String value, type;

        public Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString(){
            return value + "-" + type;
        }

        public int getValue() {
            if("AJQK".contains(value)){
                if(value == "A"){
                    return 11;
                }return 10;
            }

            return Integer.parseInt(value);

        }

        public boolean isAce() {
            return Objects.equals(value, "A");
        }

        public String getImagePath(){
            return "./Cards/" + toString() + ".png";
        }

    }

    ArrayList<Card> deck;
    Random random = new Random();

    // DEALER
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    //PLAYER
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    // WINDOW
    int boardWidth = 600;
    int boardHeight = 600;
    int cardWidth = 110;
    int cardHeight = 154;

    JFrame frame = new JFrame("Black Jack");
    JButton stayButton;
    JPanel gamePanel = new JPanel(){
        //PAINT COMPONENT
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            try {
                //draw hidden card
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./Cards/BACK.png")).getImage();
                if(!stayButton.isEnabled()){
                    hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

                // DRAW DEALERS HAND
                for(int i = 0; i < dealerHand.size(); i++){

                    Card card = dealerHand.get(i);

                    Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImage, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                }

                // DRAW PLAYERS HAND
                for(int i = 0; i < playerHand.size(); i++){

                    Card card = playerHand.get(i);

                    Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImage, 20 + (cardWidth + 5) * i, 320, cardWidth, cardHeight, null);
                }

                // DRAW SCORE
                if(!stayButton.isEnabled()){
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();

                    System.out.println("STAY:");
                    System.out.println("Dealer: " + dealerSum);
                    System.out.println("Player: " + playerSum);

                    String message = "";
                    // COMPARE RESULTS
                    //?? you or dealer has > 21
                    if(playerSum > 21) message = "You lose!!";
                    else if (dealerSum > 21) message = "You win <3 !!";

                    //both player and dealer has <= 21
                    else if (dealerSum == playerSum) message = "Draw!!";
                    else if (playerSum > dealerSum) message = "You win <3 !!";
                    else if (playerSum < dealerSum) message = "You Lose!!";

                    g.setFont(new Font("Poppins", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString( message, 220, 250);
                    g.drawString("Dealer: " + dealerSum, 10, 230);
                    g.drawString("Player: " + playerSum, 10, 280);
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };



    BlackJack(){
        startGame();
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(gamePanel);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(0, 44, 48));
        gamePanel.setPreferredSize(new Dimension(600,600));

        // BUTTON PANEL
        JPanel buttonPanel = new JPanel();
        gamePanel.add(buttonPanel, BorderLayout.SOUTH);

        // HIT BUTTON
        JButton playButton = new JButton("Play");
        playButton.setFocusable(false);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Card card = deck.removeLast();
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                gamePanel.repaint();
                if(reducePlayerAce() > 21){
                    playButton.setEnabled(false);
                }
            }
        });

        gamePanel.repaint();

        buttonPanel.add(playButton);


        // STAY BUTTON
        stayButton = new JButton("Stay");
        stayButton.setFocusable(false);
        stayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playButton.setEnabled(false);
                stayButton.setEnabled(false);

                while(dealerSum < 17){
                    Card card = deck.removeLast();
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }

                gamePanel.repaint();
            }
        });
        gamePanel.repaint();
        buttonPanel.add(stayButton);

        // RESET BUTTON
        JButton resetButton = new JButton("Reset");
        resetButton.setFocusable(false);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
                playButton.setEnabled(true);
                stayButton.setEnabled(true);
                gamePanel.repaint();
            }
        });
        buttonPanel.add(resetButton);
    }



    private void startGame() {
        // DECK
        buildDeck();
        shuffleDeck();

        // DEALER
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.removeLast(); // remove at the back of the deck
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.removeLast();
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        //PLAYER
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        for(int i =0; i < 2; i ++){
            card = deck.removeLast();
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

    }

    private void shuffleDeck() {
        for(int i = 0; i < deck.size(); i ++){
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }

    }

    private void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String type : types) {
            for (String value : values) {
                Card card = new Card(value, type);
                deck.add(card);
            }
        }

    }

    public int reducePlayerAce(){
        while(playerSum > 21 && playerAceCount > 0){
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerAce(){
        while(dealerSum > 21 && dealerAceCount > 0){
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }
}
























package VerifyAttendance;

import java.io.*;
import java.util.*;

public class Blockchain {
    private List<Block> chain;
    private String csvFileName = "blockchain.csv"; // Variable to store the CSV filename

    public Blockchain() {
        this.chain = new ArrayList<>();
        File csvFile = new File(csvFileName);
        if (csvFile.exists()) {
            loadFromCSV();
        } else {
            // Create the genesis block and save to CSV
            AttendanceRecord genesisRecord = new AttendanceRecord("0", new Date(), "Genesis", "0", "0", 0, null);
            Block genesisBlock = new Block(genesisRecord, "0");
            this.chain.add(genesisBlock);
            saveToCSV();
        }
    }

    // Load the blockchain from a CSV file
    private void loadFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // Assuming the CSV columns are: studentID, date, status, courseID, instructorID, timestamp, previousHash
                AttendanceRecord record = new AttendanceRecord(values[0], new Date(Long.parseLong(values[1])), values[2], values[3], values[4], Long.parseLong(values[5]), null);
                Block block = new Block(record, values[6]);
                this.chain.add(block);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save the blockchain to a CSV file
    public void saveToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFileName))) {
            for (Block block : chain) {
                AttendanceRecord record = block.getAttendanceRecord();
                List<String> list = new ArrayList<>();
                list.add(record.getStudentID());
                list.add(String.valueOf(record.getTimestamp())); // Saving date as timestamp for easy reconstruction
                list.add(record.getStatus());
                list.add(record.getCourseID());
                list.add(record.getInstructorID());
                list.add(String.valueOf(record.getTimestamp()));
                list.add(block.getPreviousHash());

                writer.println(String.join(",", list));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add a block to the blockchain
    public void addBlock(Block newBlock) {
        // Get the hash of the last block in the chain
        String lastHash = chain.get(chain.size() - 1).getHash();

        // Set the previous hash of the new block to be the hash of the last block
        newBlock.setPreviousHash(lastHash);

        // Recalculate the hash of the block
        newBlock.calculateHash();

        // Add the block to the chain
        chain.add(newBlock);
        
        // Save the updated blockchain to the CSV file
        saveToCSV();
    }

    // Validate the integrity of the blockchain
    public boolean validateChain() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Validate block hash
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                return false;
            }

            // Validate previous hash
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }
        }
        return true;
    }
    
    // Validate a single block by its index
    public boolean validateSingleBlock(int index) {
        if (index < 0 || index >= chain.size()) {
            return false;  // Index out of bounds
        }

        Block currentBlock = chain.get(index);
        Block previousBlock = index > 0 ? chain.get(index - 1) : null;

        // Validate block hash
        if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
            return false;
        }

        // Validate previous hash (if not the genesis block)
        if (previousBlock != null && !currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
            return false;
        }

        return true;
    }


    // Getters and Setters
    public List<Block> getChain() {
        return chain;
    }
}

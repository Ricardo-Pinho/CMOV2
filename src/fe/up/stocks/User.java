package fe.up.stocks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import android.os.Environment;
import android.util.Log;

import fe.up.stocks.Stock;

public class User implements Serializable {
	
	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	public String Name, Nickname, Password;
	public ArrayList<Stock> stocks;
	private byte[] encryptedPassword, salt;
	public User()
	{
		Name="";
		Nickname="Error";
		Password="";
		stocks = new ArrayList<Stock>();
	}
	
	public User(String Name, String Nickname, String Password)
	{
		this.Name=Name;
		this.Nickname=Nickname;
		this.Password=Password;
		stocks = new ArrayList<Stock>();
	}
	
	public User(String Name, String Nickname, String Password, String Id, byte[] encryptedPassword, byte[] salt, ArrayList<Stock> stocks)
	{
		this.Name=Name;
		this.Nickname=Nickname;
		this.Password=Password;
		this.encryptedPassword=encryptedPassword;
		this.setSalt(salt);
		this.stocks=stocks;
	}
	
	public void Save()
	{
		try{
			// Serialize data object to a file
			this.salt = generateSalt();
			this.setEncryptedPassword(getEncryptedPassword(this.Password,this.salt));
			String temp=this.Password;
			this.Password= "";
			// create a File object for the parent directory
			File Directory1 = new File(Environment.getExternalStorageDirectory()+"/Stock/");
			// have the object build the directory structure, if needed.
			if (!Directory1.exists()) {
			    Directory1.mkdirs();
			}
			File Directory2 = new File(Environment.getExternalStorageDirectory()+"/Stock/User_data/");
			// have the object build the directory structure, if needed.
			if (!Directory2.exists()) {
			    Directory2.mkdirs();
			}
			File file = new File(Environment.getExternalStorageDirectory()+"/Stock/User_data/", this.Nickname+".data");
			if (!file.exists()) {
			    file.createNewFile();
			}
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(this);
			out.close();
			this.Password=temp;
			} catch (IOException e) {
				Log.d("Error", "error Serializing User: IOException");
			} catch (NoSuchAlgorithmException e) {
				Log.d("Error", "error Serializing User: Algorithm Exception");
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				Log.d("Error", "error Serializing User: InvalidKey Exception");
				e.printStackTrace();
			}
	}
	
	public boolean Load()
	{
		try{ 
			File file = new File(Environment.getExternalStorageDirectory()+"/Stock/User_data/", this.Nickname+".data");
			FileInputStream door = new FileInputStream(file); 
			ObjectInputStream reader = new ObjectInputStream(door); 
			User x = new User(); 
			try {
				x = (User) reader.readObject();
				reader.close();
			} catch (ClassNotFoundException e) {
				Log.d("Error", "Class not found loading class User.");
			} 
			this.Name=x.Name;
			Log.d("Name", x.Name);
			this.setEncryptedPassword(x.getEncryptedPassword());
			this.setSalt(x.getSalt());
			this.stocks = x.stocks;
			return false;
		}catch (IOException e){ 
			Log.d("Error", "Error Loading User.");
			return true;
		}
	}
	
	public boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt)
			   throws NoSuchAlgorithmException, InvalidKeySpecException {
			  // Encrypt the clear-text password using the same salt that was used to
			  // encrypt the original password
			  byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);

			  // Authentication succeeds if encrypted password that the user entered
			  // is equal to the stored hash
			  return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
			 }

			 public byte[] getEncryptedPassword(String password, byte[] salt)
			   throws NoSuchAlgorithmException, InvalidKeySpecException {
			  // PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
			  // specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
			  String algorithm = "PBKDF2WithHmacSHA1";
			  // SHA-1 generates 160 bit hashes, so that's what makes sense here
			  int derivedKeyLength = 160;
			  // Pick an iteration count that works for you. The NIST recommends at
			  // least 1,000 iterations:
			  // http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
			  // iOS 4.x reportedly uses 10,000:
			  // http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
			  int iterations = 20000;

			  KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);

			  SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

			  return f.generateSecret(spec).getEncoded();
			 }

			 public byte[] generateSalt() throws NoSuchAlgorithmException {
			  // VERY important to use SecureRandom instead of just Random
			  SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

			  // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
			  byte[] salt = new byte[8];
			  random.nextBytes(salt);

			  return salt;
			 }

			public byte[] getEncryptedPassword() {
				return encryptedPassword;
			}

			public void setEncryptedPassword(byte[] encryptedPassword) {
				this.encryptedPassword = encryptedPassword;
			}

			public byte[] getSalt() {
				return salt;
			}

			public void setSalt(byte[] salt) {
				this.salt = salt;
			}
		
}

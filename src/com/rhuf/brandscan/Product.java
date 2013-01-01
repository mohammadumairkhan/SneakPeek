package com.rhuf.brandscan;

public class Product {


   private String ProductName;

   private String ProductCode;


   public Product() {
      super();
   }  

   public Product(String productname, String productcode) {
      this.ProductName = productname;
      this.ProductCode = productcode;
   }

   public String getProductName() {
      return ProductName;
   }

   public String getProductCode() {
	      return ProductCode;
	   }

   public void setProductName(String p) {
	      ProductName=p;
	   }

   public void setProductCode(String c) {
		    ProductCode = c;
		   }
}
import java.util.Scanner;

public class PharmacyOrderingSystem {

    // Scanner for user input
    static Scanner sc = new Scanner(System.in);

    // Inventory data stored in parallel arrays
    static String[] productCodes = { "P001","P002","P003","P004","P005","P006","P007","P008" };
    static String[] productNames = {
        "Paracetamol 500mg (10s)",
        "Ibuprofen 200mg (10s)",
        "Cetirizine 10mg (10s)",
        "Amoxicillin 500mg (10s)",
        "Vitamin C 500mg (20s)",
        "Loperamide 2mg (6s)",
        "Oral Rehydration Salts",
        "Antacid Tablets (10s)"
    };
    static double[] prices = { 25.00, 30.00, 18.50, 75.00, 40.00, 22.00, 15.00, 28.00 };
    static int[] stock = { 20,15,12,8,25,10,30,6 };

    // Keeps track of quantities added to the cart
    static int[] cartQty = new int[productCodes.length];

    public static void main(String[] args) {
        // Main menu loop; keeps showing until user exits
        while(true){
            System.out.println("\n=== BOTIKA NI SINTA ===");
            System.out.println("1) List products");
            System.out.println("2) Search product");
            System.out.println("3) Add to cart");
            System.out.println("4) View cart");
            System.out.println("5) Checkout");
            System.out.println("6) Reports & Restock");
            System.out.println("0) Exit\n");

            System.out.print("Choice: ");
            String choice = sc.nextLine().trim(); // remove extra spaces

            // Route to the correct feature
            if(choice.equals("1")) listProducts();
            else if(choice.equals("2")) searchProduct();
            else if(choice.equals("3")) addToCart();
            else if(choice.equals("4")) viewCart();
            else if(choice.equals("5")) checkout();
            else if(choice.equals("6")) reportsMenu();
            else if(choice.equals("0")){
                System.out.println("Goodbye."); // exit message
                break;
            } else {
                System.out.println("Invalid option."); // invalid input
            }
        }
    }

    // Displays all products in a table
    static void listProducts(){
        System.out.println("\nIdx  Code   Name                           Price   Stock");
        for(int i=0;i<productCodes.length;i++){
            System.out.printf("%-4d %-6s %-30s %7.2f %6d\n",
                    i, productCodes[i], productNames[i], prices[i], stock[i]);
        }
    }

    // Lets user search products by keyword
    static void searchProduct(){
        System.out.print("Enter keyword: ");
        String key = sc.nextLine().trim().toLowerCase(); // case-insensitive search
        boolean found=false;

        System.out.println("\nIdx  Code   Name                           Price   Stock");

        for(int i=0;i<productNames.length;i++){
            if(productNames[i].toLowerCase().contains(key)){
                // Print matching product
                System.out.printf("%-4d %-6s %-30s %7.2f %6d\n",
                        i, productCodes[i], productNames[i], prices[i], stock[i]);
                found=true;
            }
        }

        if(!found) System.out.println("No matches."); // nothing found
    }

    // Add products to cart with validation
    static void addToCart(){
        System.out.print("Enter product code: ");
        String code = sc.nextLine().trim();

        int idx = indexOfCode(code); // find product index

        if(idx==-1){ // invalid code
            System.out.println("Product not found.");
            return;
        }

        while(true){
            System.out.print("Enter quantity: ");
            String input = sc.nextLine().trim();

            try{
                int qty = Integer.parseInt(input);

                if(qty<=0){ // quantity must be positive
                    System.out.println("Quantity must be >0.");
                    continue;
                }

                int available = stock[idx] - cartQty[idx];

                // If user enters more than available, cap it instead of rejecting
                if(qty > available){
                   qty = available;
                   System.out.println("Quantity capped to available stock: " + available);
             }

                cartQty[idx]+=qty; // add to cart 
                System.out.println("Added to cart.");
                break;

            }catch(Exception e){ // handle non-numeric input
                System.out.println("Invalid number.");
            }
        }
    }

    // Display cart items and subtotal
    static void viewCart(){
        boolean empty=true;
        double subtotal=0;

        System.out.println("\nCode   Name                           Price  Qty  LineTotal");

        for(int i=0;i<cartQty.length;i++){
            if(cartQty[i]>0){
                empty=false;
                double line = prices[i]*cartQty[i]; // line total
                subtotal+=line;

                System.out.printf("%-6s %-30s %6.2f %4d %9.2f\n",
                        productCodes[i], productNames[i], prices[i], cartQty[i], line);
            }
        }

        if(empty){
            System.out.println("Your cart is empty."); // no items
            return;
        }

        System.out.printf("Subtotal: %.2f\n", subtotal);
    }

    // Checkout with discount codes
    static void checkout(){
        double subtotal = computeSubtotal();

        if(subtotal==0){
            System.out.println("Cart empty.");
            return;
        }

        System.out.print("Enter discount code: ");
        String code = sc.nextLine().trim().toUpperCase(); // normalize input

        double discountRate=0;

        if(code.equals("SENIOR20")) discountRate=0.20;
        else if(code.equals("PWD15")) discountRate=0.15;
        else if(code.equals("") || code.equals("NONE")) discountRate=0;
        else System.out.println("Invalid code. No discount.");

        double discount = subtotal*discountRate;
        double total = subtotal-discount;

        // print receipt
        System.out.println("\n=== RECEIPT ===");
        System.out.println("Code   Name                           Price  Qty  LineTotal");

        for(int i=0;i<cartQty.length;i++){
            if(cartQty[i]>0){
                double line = prices[i]*cartQty[i];
                System.out.printf("%-6s %-30s %6.2f %4d %9.2f\n",
                        productCodes[i], productNames[i], prices[i], cartQty[i], line);
            }
        }

        System.out.printf("Subtotal: %.2f\n", subtotal);
        System.out.printf("Discount: %.2f\n", discount);
        System.out.printf("Total: %.2f\n", total);

        // confirm purchase
        System.out.print("Proceed with purchase? (Y/N): ");
        String ans = sc.nextLine().trim().toUpperCase();

        if(ans.equals("Y")){
            for(int i=0;i<cartQty.length;i++){
                stock[i]-=cartQty[i]; // deduct purchased items
                cartQty[i]=0;         // clear cart
            }
            System.out.println("Purchase complete.");
        }else{
            System.out.println("Checkout cancelled."); // no changes
        }
    }

    // Reports & Restock submenu
    static void reportsMenu(){
        while(true){
            System.out.println("\n1) Low stock report (<5)");
            System.out.println("2) Restock item");
            System.out.println("0) Back");

            System.out.print("Choice: ");
            String c = sc.nextLine().trim();

            if(c.equals("1")) lowStock();
            else if(c.equals("2")) restockItem();
            else if(c.equals("0")) break; // back to main menu
            else System.out.println("Invalid option.");
        }
    }

    // Show items with stock <5
    static void lowStock(){
        boolean found=false;
        System.out.println("\nIdx  Code   Name                           Price   Stock");

        for(int i=0;i<stock.length;i++){
            if(stock[i]<5){
                System.out.printf("%-4d %-6s %-30s %7.2f %6d\n",
                        i, productCodes[i], productNames[i], prices[i], stock[i]);
                found=true;
            }
        }

        if(!found) System.out.println("No low stock items.");
    }

    // Restock a specific item
    static void restockItem(){
        System.out.print("Enter product code: ");
        String code = sc.nextLine().trim();

        int idx=indexOfCode(code);
        if(idx==-1){
            System.out.println("Product not found.");
            return;
        }

        while(true){
            System.out.print("Enter restock quantity: ");
            String input = sc.nextLine().trim();

            try{
                int qty = Integer.parseInt(input);

                if(qty<=0){
                    System.out.println("Must be positive.");
                    continue;
                }

                stock[idx]+=qty; // add to stock
                System.out.println("Restocked.");
                break;

            }catch(Exception e){
                System.out.println("Invalid number.");
            }
        }
    }

    // Helper: find index of a product by code
    static int indexOfCode(String code){
        for(int i=0;i<productCodes.length;i++){
            if(productCodes[i].equalsIgnoreCase(code)){
                return i;
            }
        }
        return -1; // not found
    }

    // Helper: compute subtotal of current cart
    static double computeSubtotal(){
        double sum=0;
        for(int i=0;i<cartQty.length;i++){
            sum+=prices[i]*cartQty[i];
        }
        return sum;
    }
}
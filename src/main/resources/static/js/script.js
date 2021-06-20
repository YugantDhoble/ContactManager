/* Set the width of the side navigation to 250px and the left margin of the page content to 250px and add a black background color to body */

const toggleSidebar =() =>{
	if($(".sidebar").is(":visible")){
		$(".sidebar").css("display","none")
		$(".content").css("margin-left","0%");
		$("body").css("backgroundColor" , "#e2e2e2");
	}else{
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
		$("body").css("backgroundColor" , "rgba(0,0,0,0.7)");
	}
}

const search=()=>{
	
	let query=$("#search-input").val();
	
	if(query==''){
		$(".search-result").hide();
	}else{
		
		let url=`http://localhost:8080/search/${query}`;
		
		fetch(url).then((response)=>{
			return response.json();
		}).then((data)=>{
			
			let text=`<div class='list-group'>`;
			
			data.forEach((contact)=>{
				text+=`<a href='/user/${contact.cId}/contact' class'list-group-item list-group-action'>${contact.name}</a>`
			});
			
			text+=`</div>`;
			
			$(".search-result").html(text);
			$(".search-result").show();

		});
		
		
		$(".search-result").show();
	}
};


//first request  to server t create order

const paymentStart=()=>{
	console.log("Payment Started....");
	let amount=$("#payment_feild").val();
	console.log(amount);
	if(amount==""||amount==null){
		  swal("Failed!", " Amount is required!!", "error");
		return;
	}


	$.ajax(
		{
			url:'/user/create_order',
			data:JSON.stringify({amount:amount,info:'order_request'}),
			contentType : 'application/json',
			type:'POST',
			dataType:'json',
			success:function(response){
				console.log(response)
				if(response.status=="created"){
					//open payment from
					let options={
						key:"rzp_test_qe1X5poeDFu4p5",
						amount: response.amount,
						currency:"INR",
						name:"Smart Contact Manager",
						description:"Donation",
						image:"https://thumbs.dreamstime.com/b/digital-wallet-e-payment-logo-design-vector-illustration-online-electronic-bank-network-buy-coin-bitcoin-exchange-background-phone-168953702.jpg",
						order_id:response.id,
						handler: function (response){
							console.log(response.razorpay_payment_id);
							console.log(response.razorpay_order_id);
							console.log(response.razorpay_signature)
							swal("Good job!", " Congrates !! Payment Successful !!!", "success");

							//alert("Congrates !! Payment Successful !!")
							},
						prefill: {
							name: "",
							email: "",
							contact: "",
							},
						notes:{
							address:"Yugant Dhoble",
						},
						theme:{
							color:"#3399cc",
						},
						   
				};

				let rzp=new Razorpay(options);

				rzp.on("payment.failed", function (response){
					console.log(response.error.code);
					console.log(response.error.description);
					console.log(response.error.source);
					console.log(response.error.step);
					console.log(response.error.reason);
					console.log(response.error.metadata.order_id);
					console.log(response.error.metadata.payment_id);
				    swal("Failed!", " Payment Failed", "error");
				   });
				   



				rzp.open();





			}
			},
			error:function(error){
				console.log(error)
				    swal("Failed!", "Something went wrong!!!!", "error");
			}

		}
	)
};




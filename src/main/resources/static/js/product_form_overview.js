dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function() {
    $("#shortDescription").richText();
    $("#fullDescription").richText();

    dropdownBrands.change(function() {
        dropdownCategories.empty();
        getCategories();
    });

    getCategoriesForNewForm();
});

function getCategoriesForNewForm() {
    catIdField = $("#categoryId");
    console.log("test");
    editMode = false;

    if (catIdField.length) {
        editMode = true;
    }

    if (!editMode) getCategories();
}

function getCategories() {
    brandId = dropdownBrands.val();/* .text -> tra ve name */
    url = brandModuleURL + "/" + brandId + "/categories";/* brandModuleURL: ben product_form thymlead, "brandId + "/categories"": ben rest controller */

    $.get(url, function(responseJson){/*$.get chi truyen url(2 tham so), $.post(3 tham so) truyen url va data->dung de delete, edit...  */
        $.each(responseJson, function(index, category) {
            $("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
        });
    });
}
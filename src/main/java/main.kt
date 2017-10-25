import dog.librewulf.teeoc.Swapper

private val pathFromSteamRoot = "SteamApps/common/The Jackbox Party Pack 3/games/" +
        "AwShirt/TalkshowExport/project/actions/5695.swf"

fun main(args: Array<String>) {
    val res = Swapper.replaceImageAsset(
        "/home/william/tmp/jpexs-decompiler/5696.swf",
        "/home/william/tmp/jpexs-decompiler/out.swf",
        496,
        "/home/william/Pictures/Furry/Commissions/libreacbadge.png"
    )

    println("Result: $res");
}
